package br.edu.infnet.githubactionsexample;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.edu.infnet.githubactionsexample.controller.UserController;
import br.edu.infnet.githubactionsexample.model.User;
import br.edu.infnet.githubactionsexample.service.UserService;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

  @InjectMocks private UserController userController;

  @Mock private UserService userService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
  }

  @Test
  void testGetAllUsers() throws Exception {
    User user1 = new User(1L, "John Doe", "john@example.com");
    User user2 = new User(2L, "Jane Doe", "jane@example.com");

    when(userService.findAll()).thenReturn(Arrays.asList(user1, user2));

    mockMvc
        .perform(get("/api/users").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].name").value("John Doe"))
        .andExpect(jsonPath("$[1].name").value("Jane Doe"));
  }

  @Test
  void testGetUserById() throws Exception {
    User user = new User(1L, "John Doe", "john@example.com");

    when(userService.findById(1L)).thenReturn(Optional.of(user));

    mockMvc
        .perform(get("/api/users/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.email").value("john@example.com"));
  }

  @Test
  void testGetUserById_NotFound() throws Exception {
    when(userService.findById(1L)).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/api/users/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void testCreateUser() throws Exception {
    User user = new User(null, "John Doe", "john@example.com");
    User createdUser = new User(1L, "John Doe", "john@example.com");

    when(userService.save(any(User.class))).thenReturn(createdUser);

    mockMvc
        .perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John Doe\",\"email\":\"john@example.com\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.email").value("john@example.com"));
  }

  @Test
  void testUpdateUser() throws Exception {
    User existingUser = new User(1L, "John Doe", "john@example.com");
    User updatedUser = new User(1L, "John Doe Updated", "john.updated@example.com");

    when(userService.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userService.save(any(User.class))).thenReturn(updatedUser);

    mockMvc
        .perform(
            put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John Doe Updated\",\"email\":\"john.updated@example.com\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("John Doe Updated"))
        .andExpect(jsonPath("$.email").value("john.updated@example.com"));
  }

  @Test
  void testUpdateUser_NotFound() throws Exception {
    when(userService.findById(1L)).thenReturn(Optional.empty());

    mockMvc
        .perform(
            put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John Doe\",\"email\":\"john@example.com\"}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteUser() throws Exception {
    mockMvc
        .perform(delete("/api/users/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }
}
