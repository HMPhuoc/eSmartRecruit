package com.example.eSmartRecruit.services.impl;

import com.example.eSmartRecruit.authentication.request_reponse.RegisterRequest;
import com.example.eSmartRecruit.controllers.request_reponse.ResponseObject;
import com.example.eSmartRecruit.controllers.request_reponse.request.EditUserRequest;
import com.example.eSmartRecruit.controllers.request_reponse.request.UserRequest;
import com.example.eSmartRecruit.exception.UserException;
import com.example.eSmartRecruit.models.User;
import com.example.eSmartRecruit.models.enumModel.Role;
import com.example.eSmartRecruit.models.enumModel.UserStatus;
import com.example.eSmartRecruit.repositories.UserRepos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService;
    private UserRepos userRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepos.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void testGetAllUser() throws UserException {
        // Arrange
        List<User> expectedUsers = new ArrayList<>();
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> result = userService.getAllUser();

        // Assert
        assertEquals(expectedUsers, result);
    }

    @Test
    void testFindByUsername_Successful() throws UserException {
        // Arrange
        String username = "john_doe";
        User expectedUser = new User();
        expectedUser.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = userService.findByUsername(username);

        // Assert
        assertEquals(expectedUser, result);
    }

    @Test
    void testFindByUsername_UserNotFound() {
        // Arrange
        String username = "non_existent_user";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserException.class, () -> userService.findByUsername(username));
    }
    @Test
    void testGetUserRole() {
        // Arrange
        int userId = 1;
        User user = new User();
        user.setId(userId);
        user.setRoleName(Role.Candidate);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        String result = userService.getUserRole(userId);

        // Assert
        assertEquals("Candidate", result);
    }
    @Test
    void testUpdateUserPassword_Successful() throws UserException {
        // Arrange
        String username = "john_doe";
        String newPassword = "new_password";
        User expectedUser = new User();
        expectedUser.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedUser));
        when(passwordEncoder.encode(newPassword)).thenReturn("encoded_password");

        // Act
        String result = userService.updateUserpassword(username, newPassword);

        // Assert
        assertEquals("Updated Successfully", result);
        verify(userRepository, times(1)).save(expectedUser);
    }
    @Test
    void testUpdateUserPassword_UserNotFound() {
        // Arrange
        String username = "non_existent_user";
        String newPassword = "new_password";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserException.class, () -> userService.updateUserpassword(username, newPassword));
        verify(userRepository, never()).save(any());
    }
    @Test
    void testUpdateUser_DuplicateEmail() {
        // Arrange
        int userId = 1;
        UserRequest userRequest = new UserRequest("duplicate_email@example.com", "new_phone_number");
        User existingUser = new User();
        existingUser.setUsername("john_doe");
        existingUser.setEmail("old_email@example.com");
        existingUser.setPhoneNumber("old_phone_number");
        existingUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.of(new User()));

        // Act and Assert
        assertThrows(UserException.class, () -> userService.updateUser(userRequest, userId));
        verify(userRepository, never()).save(any());
    }
    @Test
    void testUpdateUser_DuplicatePhoneNumber() {
        // Arrange
        int userId = 1;
        UserRequest userRequest = new UserRequest("new_email@example.com", "duplicate_phone_number");
        User existingUser = new User();
        existingUser.setUsername("john_doe");
        existingUser.setEmail("old_email@example.com");
        existingUser.setPhoneNumber("old_phone_number");
        existingUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByPhoneNumber(userRequest.getPhoneNumber())).thenReturn(Optional.of(new User()));

        // Act and Assert
        assertThrows(UserException.class, () -> userService.updateUser(userRequest, userId));
        verify(userRepository, never()).save(any());
    }
    @Test
    void testCheckDuplicatePhone_DuplicatePhone() {
        // Arrange
        String phoneNumber = "123456789";
        User user = new User();
        user.setPhoneNumber(phoneNumber);
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(new User()));

        // Act
        String result = userService.checkDuplicatePhone(user);

        // Assert
        assertEquals("This phone number is already used by another user!", result);
    }
    @Test
    void testCheckDuplicatePhone_NonDuplicatePhone() {
        // Arrange
        String phoneNumber = "123456789";
        User user = new User();
        user.setPhoneNumber(phoneNumber);
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        // Act
        String result = userService.checkDuplicatePhone(user);

        // Assert
        assertNull(result);
    }
    @Test
    void testCheckDuplicate_UsernameExists() {
        // Arrange
        String username = "john_doe";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        // Act
        String result = userService.checkDuplicate(user);

        assertEquals("This name already exist!", result);
    }
    @Test
    void testCheckDuplicate_EmailExists() {
        // Arrange
        String email = "john@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        // Act
        String result = userService.checkDuplicate(user);

        // Assert
        assertEquals("This email is already used by another user!", result);
    }
    @Test
    void testCheckDuplicate_PhoneExists() {
        // Arrange
        String phoneNumber = "123456789";
        User user = new User();
        user.setPhoneNumber(phoneNumber);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(new User()));

        // Act
        String result = userService.checkDuplicate(user);

        // Assert
        assertEquals("This phone number is already used by another user!", result);
    }
    @Test
    void testCheckDuplicate_NonDuplicate() {
        // Arrange
        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setPhoneNumber("123456789");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(user.getPhoneNumber())).thenReturn(Optional.empty());

        // Act
        String result = userService.checkDuplicate(user);

        // Assert
        assertNull(result);
    }
    @Test
    void testIsEnabled_UserEnabled() throws UserException {
        // Arrange
        int userId = 1;
        User user = new User();
        user.setId(userId);
        user.setStatus(UserStatus.Active);
        user.setEnabled(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // Act
        boolean result = userService.isEnabled(userId);

        // Assert
        System.out.println("Result: " + result);
        assertTrue(result);
    }
    @Test
    void testIsEnabled_UserNotEnabled() throws UserException {
        // Arrange
        int userId = 2;
        User user = new User();
        user.setId(userId);
        user.setEnabled(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        boolean result = userService.isEnabled(userId);

        // Assert
        assertFalse(result);
    }
    @Test
    void testIsEnabled_UserNotFound() {
        // Arrange
        int userId = 3;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserException.class, () -> userService.isEnabled(userId));
    }
    @Test
    void testGetUserById_UserFound() throws UserException {
        // Arrange
        int userId = 4;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
    }
    @Test
    void testGetUserById_UserNotFound() {
        // Arrange
        int userId = 5;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserException.class, () -> userService.getUserById(userId));
    }
    @Test
    void testUpdateUser_SuccessfulUpdate() throws UserException {
        // Arrange
        UserRequest userRequest = new UserRequest("new_email@example.com", "new_phone_number");
        User existingUser = new User();
        existingUser.setUsername("john_doe");
        existingUser.setEmail("old_email@example.com");
        existingUser.setPhoneNumber("old_phone_number");
        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        User result = userService.updateUser(userRequest, 1);

        // Assert
        assertEquals(userRequest.getEmail(), result.getEmail());
        assertEquals(userRequest.getPhoneNumber(), result.getPhoneNumber());
    }

    @Test
    void testGetCountUser() {
        // Arrange
        when(userRepository.count()).thenReturn(10L); // Giả lập kết quả trả về

        // Act
        Long result = userService.getcountUser();

        // Assert
        assertEquals(10L, result); // Kiểm tra xem kết quả có phải là 10L như mong đợi hay không
        verify(userRepository, times(1)).count(); // Kiểm tra xem phương thức count đã được gọi một lần hay không
    }
    @Test
    void testSaveUser_Success() throws UserException {
        // Arrange
        RegisterRequest request = new RegisterRequest("john_doe", "john_doe@example.com", "John@123", "0123456789","Candidate");

        ResponseObject result = userService.saveUser(request);
        assertEquals(result.getStatus(),"SUCCESS");
//        // Assert
        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
        assertEquals("Create user successfully!", result.getMessage());
        verify(userRepository, times(1)).save(any());
    }
    @Test
    void testFindById_UserExists() throws UserException {
        // Arrange
        int userId = 1;
        User expectedUser = new User(userId, "john_doe", "john_doe@example.com","John@123", "0123456789",Role.Candidate,UserStatus.Active, Date.valueOf("2023-11-02"), Date.valueOf("2023-11-02"));

        // Mocking the behavior of userRepository.findById
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // Act
        User actualUser = userService.findById(userId);

        // Assert
        assertEquals(expectedUser, actualUser);

        // Verify that userRepository.findById was called exactly once with the correct userId
        verify(userRepository, times(1)).findById(userId);
    }
    @Test
    void testFindById_UserNotFound() {
        // Arrange
        int userId = 2;

        // Mocking the behavior of userRepository.findById to return an empty Optional
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act and Assert
        UserException exception = assertThrows(UserException.class, () -> userService.findById(userId));

        // Verify that userRepository.findById was called exactly once with the correct userId
        verify(userRepository, times(1)).findById(userId);

        // Verify that the exception message is correct
        assertEquals("User Not Found", exception.getMessage());
    }

    @Test
    public void testEditUser_DuplicateUsername() {
        // Arrange
        int userId = 1;
        EditUserRequest editUserRequest = new EditUserRequest("john_doe", "newpassword", "john.doe@example.com", "1234567890", "Candidate", "Active");
        User existingUser = new User(userId, "john_doe", "password", "john.doe@example.com", "1234567890", Role.Candidate, UserStatus.Active, Date.valueOf("2021-01-12"), Date.valueOf("2021-01-02"));

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername(editUserRequest.getUsername())).thenReturn(Optional.of(existingUser));

        // Act and Assert
        assertThrows(UserException.class, () -> userService.editUser(userId, editUserRequest));
    }

    @Test
    public void testEditUser_SuccessfulEdit() throws UserException {
        // Arrange
        int userId = 1;
        EditUserRequest editUserRequest = new EditUserRequest("john_doe", "newpassword", "john.doe@example.com", "1234567890", "Candidate", "Active");
        User existingUser = new User(userId, "john_doe", "password", "john.doe@example.com", "1234567890", Role.Candidate, UserStatus.Active, Date.valueOf("2021-01-12"), Date.valueOf("2021-01-02"));

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // Act
        User updatedUser = userService.editUser(userId, editUserRequest);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
        assertNotNull(updatedUser);
        assertEquals(editUserRequest.getUsername(), updatedUser.getUsername());
        // Additional assertions for other fields
    }
}