package com.circlerate.circle_rate.user.service;

import com.circlerate.circle_rate.auth.model.AccessToken;
import com.circlerate.circle_rate.auth.model.LoginType;
import com.circlerate.circle_rate.auth.model.RefreshToken;
import com.circlerate.circle_rate.auth.payload.AuthResponse;
import com.circlerate.circle_rate.auth.payload.LoginRequest;
import com.circlerate.circle_rate.auth.payload.SignupRequest;
import com.circlerate.circle_rate.auth.repository.RefreshTokenRepository;
import com.circlerate.circle_rate.auth.service.JwtService;
import com.circlerate.circle_rate.common.constants.ResponseMessage;
import com.circlerate.circle_rate.user.model.Interest;
import com.circlerate.circle_rate.user.model.User;
import com.circlerate.circle_rate.user.model.UserDao;
import com.circlerate.circle_rate.user.payload.InterestRequest;
import com.circlerate.circle_rate.user.payload.ProfilePictureUploadRequest;
import com.circlerate.circle_rate.user.payload.UserProfileUpdateRequest;
import com.circlerate.circle_rate.listing.model.property.Property;
import com.circlerate.circle_rate.listing.model.property.dto.PropertyDto;
import com.circlerate.circle_rate.listing.payload.PresignedUrlResponse;
import com.circlerate.circle_rate.listing.repository.ResidentialPropertyRepository;
import com.circlerate.circle_rate.listing.repository.CommercialPropertyRepository;
import com.circlerate.circle_rate.listing.repository.LandPropertyRepository;
import com.circlerate.circle_rate.listing.repository.PropertyQueryRepository;
import com.circlerate.circle_rate.listing.utils.PropertyUtils;
import com.circlerate.circle_rate.user.repository.InterestRepository;
import com.circlerate.circle_rate.common.exception.custom_exception.UserNotFoundException;
import com.circlerate.circle_rate.user.repository.UserRepository;
import com.circlerate.circle_rate.user.utils.UserServiceUtils;
import com.circlerate.circle_rate.common.utils.S3Service;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.circlerate.circle_rate.common.exception.custom_exception.InterestAlreadyExistsException;
import com.circlerate.circle_rate.common.exception.custom_exception.InterestNotFoundException;
import com.circlerate.circle_rate.common.exception.custom_exception.PropertyNotFoundException;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.circlerate.circle_rate.config.ApplicationConfig.isLocal;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserServiceUtils userServiceUtils;
    private final InterestRepository interestRepository;
    private final ResidentialPropertyRepository residentialPropertyRepository;
    private final CommercialPropertyRepository commercialPropertyRepository;
    private final LandPropertyRepository landPropertyRepository;
    private final PropertyQueryRepository propertyQueryRepository;
    private final PropertyUtils propertyUtils;
    private final S3Service s3Service;

    



    public ResponseEntity<AuthResponse> signup(SignupRequest request, HttpServletResponse response) {
        request.setLoginType(LoginType.CUSTOM);
        User user =userServiceUtils.createUser(request);
        AccessToken accessToken = jwtService.generateAccessToken(user.getId(), user.getRole());
        RefreshToken refreshToken = jwtService.generateRefreshToken(user.getId());
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(!isLocal) // Set to false in local dev if not using HTTPS
                .path("/auth")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        if((user.getRole() != request.getRole())){
            log.info("user created without role: {} permissions", request.getRole() );
            return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(user.getId(), user.getEmail(), ResponseMessage.APPROVAL_RAISED, accessToken.getToken()));
        }
        log.info("User Successfully Created");
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(user.getId(), user.getEmail(), ResponseMessage.USER_CREATED, accessToken.getToken()));
    }


    public ResponseEntity<AuthResponse> login(LoginRequest request, HttpServletResponse response) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if(optionalUser.isEmpty()){
            log.info("Could not find any user with email : {}",request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(request.getEmail(),ResponseMessage.USER_NOT_FOUND));
        }
        User userFromRepo = optionalUser.get();
        if(!userFromRepo.getLoginType().equals(LoginType.CUSTOM)){
            log.info("User logged in with different login type: {}", userFromRepo.getLoginType());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new AuthResponse(request.getEmail(), ResponseMessage.USER_SIGNEDUP_WITH_DIFFERENT_LOGIN_TYPE + userFromRepo.getLoginType()));
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userFromRepo.getId(),
                        request.getPassword()
                )
        );

        // At this point, authentication is successful
        User user = (User) authentication.getPrincipal();
        AccessToken accessToken = jwtService.generateAccessToken(user.getId(), user.getRole());
        RefreshToken refreshToken = jwtService.generateRefreshToken(user.getId());
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(!isLocal) // Set to false in local dev if not using HTTPS
                .path("/auth")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        log.info("User Successfully logged in with user email: {}", user.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(user.getId(), user.getEmail(), ResponseMessage.USER_SUCCESSFULLY_LOGGED_IN, accessToken.getToken()));
    }

    public ResponseEntity<String> logout(String refreshToken, HttpServletResponse response) {
        try {
            String tokenId = jwtService.extractTokenId(refreshToken);
            refreshTokenRepository.deleteById(tokenId);
        } catch (Exception ex) {
            log.warn("Logout: invalid refresh token: {}", ex.getMessage());
            throw ex;
        }
        // Clear refresh token cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(!isLocal) // Set to false in local dev if not using HTTPS
                .path("/auth")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok("Logged out successfully.");
    }

    public ResponseEntity<AuthResponse> refreshAccessToken(String refreshToken, HttpServletResponse response) {
        try {
            String userId = jwtService.extractUsername(refreshToken);
            String tokenId = jwtService.extractTokenId(refreshToken);
            
            // Check if refresh token exists
            if (!refreshTokenRepository.existsById(tokenId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, ResponseMessage.INVALID_TOKEN));
            }
            
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, ResponseMessage.USER_NOT_FOUND));
            }
            
            // Check if refresh token is older than 5 days
            Optional<RefreshToken> existingToken = refreshTokenRepository.findById(tokenId);
            boolean shouldRotateToken = false;
            
            if (existingToken.isPresent()) {
                long daysSinceIssued = (System.currentTimeMillis() - existingToken.get().getIssuedAt().getTime()) / (1000 * 60 * 60 * 24);
                shouldRotateToken = daysSinceIssued >= 5;
            }
            
            AccessToken newAccessToken = jwtService.generateAccessToken(user.getId(), user.getRole());
            AuthResponse authResponse = new AuthResponse(user.getId(), user.getEmail(), ResponseMessage.ACCESS_TOKEN_REFRESHED, newAccessToken.getToken());
            
            // If token is older than 5 days, generate new refresh token and delete old one
            if (shouldRotateToken) {
                // Delete old refresh token
                refreshTokenRepository.deleteById(tokenId);
                // Generate new refresh token
                RefreshToken newRefreshToken = jwtService.generateRefreshToken(user.getId());
                
                // Set new refresh token as cookie
                ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken.getToken())
                        .httpOnly(true)
                        .secure(!isLocal) // Set to false in local dev if not using HTTPS
                        .path("/auth")
                        .maxAge(Duration.ofDays(7))
                        .sameSite("Strict")
                        .build();
                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            }
            
            return ResponseEntity.ok(authResponse);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, ResponseMessage.INVALID_TOKEN));
        }
    }

    public ResponseEntity<String> revokeRefreshToken(String refreshToken) {
        try {
            String tokenId = jwtService.extractTokenId(refreshToken);
            if (refreshTokenRepository.existsById(tokenId)) {
                refreshTokenRepository.deleteById(tokenId);
                return ResponseEntity.ok("Refresh token revoked successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Refresh token not found");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }


    
    public ResponseEntity<String> showInterestInProperty(String userId, InterestRequest request) {
        if (!propertyQueryRepository.propertyExistsByIdAndType(request.getPropertyId(), request.getPropertyType().name())) {
            throw new PropertyNotFoundException(ResponseMessage.PROPERTY_NOT_FOUND);
        }

        if (interestRepository.existsByUserIdAndPropertyId(userId, request.getPropertyId())) {
            throw new InterestAlreadyExistsException(ResponseMessage.INTEREST_ALREADY_EXISTS);
        }
        try{
            Interest interest = new Interest(userId, request);
            interestRepository.save(interest);
            return ResponseEntity.ok(ResponseMessage.INTEREST_ADDED);
        } catch (Exception ex) {
            log.error("Error adding interest: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding interest: "+ex.getMessage());
        }
    }

    public ResponseEntity<List<PropertyDto>> getUserInterestedProperties(String userId) {
        try {
            List<Interest> interests = interestRepository.findByUserId(userId);
            log.info("Interest List Size :{} ",interests.size());
            List<Property> properties = findPropertiesByInterests(interests);
            List<PropertyDto> propertyDtos = propertyUtils.mapPropertyListToPropertyDtoList(properties);
            return ResponseEntity.ok(propertyDtos);
        } catch (Exception ex) {
            log.error("Error fetching interested properties: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> removeInterestFromProperty(String userId, String propertyId) {
        try {
            if (!interestRepository.existsByUserIdAndPropertyId(userId, propertyId)) {
                throw new InterestNotFoundException(ResponseMessage.INTEREST_NOT_FOUND);
            }
            interestRepository.deleteByUserIdAndPropertyId(userId, propertyId);
            log.info("Interest removed successfully for user: {} and property: {}", userId, propertyId);
            return ResponseEntity.ok(ResponseMessage.INTEREST_REMOVED);
        } catch (Exception ex) {
            log.error("Error removing interest: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing interest");
        }
    }

    
    public ResponseEntity<UserDao> getUserProfile(String userId) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                throw new UserNotFoundException(ResponseMessage.USER_NOT_FOUND);
            }
        
            return ResponseEntity.ok(new UserDao(user.get()));
        } catch (Exception ex) {
            log.error("Error fetching user profile: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<UserDao> updateUserProfile(String userId, UserProfileUpdateRequest request) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new UserNotFoundException(ResponseMessage.USER_NOT_FOUND);
            }
            
            User user = userOpt.get();
            userServiceUtils.updateUserProfile(user, request);
            
            User updatedUser = userRepository.save(user);
            log.info("User profile updated successfully: {}", updatedUser.getEmail());
            return ResponseEntity.ok(new UserDao(updatedUser));
        } catch (Exception ex) {
            log.error("Error updating user profile: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private List<Property> findPropertiesByInterests(List<Interest> interests) {
        List<Property> properties = new ArrayList<>();
        
        for (Interest interest : interests) {
            switch (interest.getPropertyType().name()) {
                case "RESIDENTIAL" ->
                    residentialPropertyRepository.findById(interest.getPropertyId())
                            .ifPresent(properties::add);
                case "COMMERCIAL" ->
                    commercialPropertyRepository.findById(interest.getPropertyId())
                            .ifPresent(properties::add);

                case "LAND" ->
                    landPropertyRepository.findById(interest.getPropertyId())
                            .ifPresent(properties::add);
            }
        }
        
        return properties;
    }

    public ResponseEntity<PresignedUrlResponse> uploadProfilePicture(String userId, ProfilePictureUploadRequest request) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new UserNotFoundException(ResponseMessage.USER_NOT_FOUND);
            }

            if (!isValidImageMimeType(request.getMimeType())) {
                log.error("Invalid image MIME type: {}", request.getMimeType());
                return ResponseEntity.badRequest().build();
            }

            String s3Key = generateProfilePictureS3Key(userId, request.getMimeType());
            String presignedUrl = s3Service.generatePresignedUrl(s3Key, request.getMimeType());
            
            return ResponseEntity.ok(new PresignedUrlResponse(presignedUrl, s3Key));
        } catch (Exception ex) {
            log.error("Error generating profile picture upload URL: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> updateProfilePictureKey(String userId, String s3Key) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new UserNotFoundException(ResponseMessage.USER_NOT_FOUND);
            }

            User user = userOpt.get();
            if (user.getProfilePictureKey() != null && !user.getProfilePictureKey().isEmpty() && !user.getProfilePictureKey().equals(s3Key)) {
                s3Service.deleteObject(user.getProfilePictureKey());
                log.info("Deleted old profile picture: {}", user.getProfilePictureKey());
            }
            user.setProfilePictureKey(s3Key);
            userRepository.save(user);
            log.info("Profile picture key updated successfully: {}", s3Key);
            return ResponseEntity.ok("Profile picture key updated successfully");
        } catch (Exception ex) {
            log.error("Error updating profile picture key: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating profile picture");
        }
    }

    public ResponseEntity<String> deleteProfilePicture(String userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new UserNotFoundException(ResponseMessage.USER_NOT_FOUND);
            }
            User user = userOpt.get();
            String profilePictureKey = user.getProfilePictureKey();
            
            if (profilePictureKey != null && !profilePictureKey.isEmpty()) {
                s3Service.deleteObject(profilePictureKey);
                user.setProfilePictureKey(null);
                userRepository.save(user);
            }
            log.info("Profile picture deleted successfully: {}", profilePictureKey);
            return ResponseEntity.ok("Profile picture deleted successfully");
        } catch (Exception ex) {
            log.error("Error deleting profile picture: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting profile picture");
        }
    }

    private boolean isValidImageMimeType(String mimeType) {
        return mimeType != null && (
            mimeType.equals("image/jpeg") ||
            mimeType.equals("image/jpg") ||
            mimeType.equals("image/png") ||
            mimeType.equals("image/gif") ||
            mimeType.equals("image/webp")
        );
    }

    private String generateProfilePictureS3Key(String userId, String mimeType) {
        String extension = getImageExtension(mimeType);
        return String.format("owner/%s/profilePicture%s", userId, extension);
    }

    private String getImageExtension(String mimeType) {
        if (mimeType == null) return ".jpg";
        return switch (mimeType) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }

}
