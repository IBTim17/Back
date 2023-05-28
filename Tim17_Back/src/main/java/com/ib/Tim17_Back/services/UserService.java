package com.ib.Tim17_Back.services;

import com.ib.Tim17_Back.dtos.*;
import com.ib.Tim17_Back.enums.UserRole;
import com.ib.Tim17_Back.exceptions.CustomException;
import com.ib.Tim17_Back.exceptions.IncorrectCodeException;
import com.ib.Tim17_Back.exceptions.InvalidCredentials;
import com.ib.Tim17_Back.exceptions.UserNotFoundException;
import com.ib.Tim17_Back.models.ResetCode;
import com.ib.Tim17_Back.models.User;
import com.ib.Tim17_Back.repositories.UserRepository;
import com.ib.Tim17_Back.security.SaltGenerator;
import com.ib.Tim17_Back.security.SecurityUser;
import com.ib.Tim17_Back.security.UserFactory;
import com.ib.Tim17_Back.security.jwt.JwtTokenUtil;
import com.ib.Tim17_Back.services.interfaces.IUserService;
import com.sendgrid.*;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SaltGenerator saltGenerator;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    @Value("${SENDGRID_API_KEY}")
    private String SENDGRID_API_KEY;
    @Value("${SENDER_EMAIL}")
    private String SENDER_EMAIL;
    @Value("${TWILIO_ACCOUNT_SID}")
    private String TWILIO_ACCOUNT_SID;
    @Value("${TWILIO_AUTH_TOKEN}")
    private String TWILIO_AUTH_TOKEN;

    public UserService(UserRepository userRepository, SaltGenerator saltGenerator, JwtTokenUtil jwtTokenUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.saltGenerator = saltGenerator;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public SecurityUser findByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with username '%s' is not found!", username)));

        return UserFactory.create(user);
    }

    @Override
    public TokenDTO logIn(String email, String password) throws Exception {

        if(!this.verifyPassword(email, password)) throw new CustomException(";fehfehoehf");
        System.out.println("jeeej");

        SecurityUser userDetails = (SecurityUser) this.findByUsername(email);
        if(!this.userRepository.findByEmail(email).get().isActivated())  throw new CustomException("Not verified!");
        TokenDTO token = new TokenDTO();
        String tokenValue = this.jwtTokenUtil.generateToken(userDetails);
        token.setToken(tokenValue);
        token.setRefreshToken(this.jwtTokenUtil.generateRefreshToken(userDetails));
        Authentication authentication =
                this.authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(email, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return token;
    }

    @Override
    public UserDTO register(CreateUserDTO createUserDTO) throws NoSuchAlgorithmException {

    //    validateRegistration(createUserDTO);

        User user = new User();
        user.setFirstName(createUserDTO.getFirstName());
        user.setLastName(createUserDTO.getLastName());
        user.setEmail(createUserDTO.getEmail());
        user.setActivated(false);
        user.setPasswordLastChanged(LocalDateTime.now());
        user.setRole(UserRole.USER);
        user.setPhoneNumber(createUserDTO.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
        user = userRepository.save(user);

        try {
            this.sendRegistrationEmail(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new UserDTO(user);
    }

    public void resetPassword(PasswordResetRequestDTO passwordResetRequest) throws IncorrectCodeException, UserNotFoundException {
        Optional<User> userDB = userRepository.findByEmail(passwordResetRequest.getEmail());
        if (userDB.isEmpty()) throw new UserNotFoundException();

        User user = userDB.get();
        if (user.getPasswordResetCode().getCode().equals(passwordResetRequest.getCode()) && user.getPasswordResetCode().getExpirationDate().isAfter(LocalDateTime.now())) {
            user.setPassword(passwordEncoder.encode(passwordResetRequest.getNewPassword()));
            userRepository.save(user);
        } else {
            throw new IncorrectCodeException();
        }
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }


    @Override
    public void confirmAccount(AccountConfirmationDTO accountConfirmationDTO) {
        System.out.println(accountConfirmationDTO.getEmail() + "  " + accountConfirmationDTO.getCode());
        Optional<User> userDB = userRepository.findByEmail(accountConfirmationDTO.getEmail());
        if (userDB.isEmpty()) throw new UserNotFoundException();
        User user = userDB.get();
        System.out.println(accountConfirmationDTO.getEmail() + "  " + accountConfirmationDTO.getCode());
        if (user.getPasswordResetCode().getCode().equals(accountConfirmationDTO.getCode()) && user.getPasswordResetCode().getExpirationDate().isAfter(LocalDateTime.now())) {
            user.setActivated(true);
            System.out.println(user.isActivated());
            userRepository.save(user);
        } else {
            throw new IncorrectCodeException();
        }
    }

    public void sendPasswordResetCode(ResetPasswordDTO body) throws UserNotFoundException, IOException {
        System.out.println(body.getResource());
        if (isEmail(body.getResource())) {
            sendByEmail(body.getResource());
        } else if (isPhoneNumber(body.getResource())) {
            sendByPhone(body.getResource());
        } else throw new InvalidCredentials();
    }

    private void sendByPhone(String phone) {
        Optional<User> userDB = userRepository.findByPhoneNumber(phone);
        if (userDB.isEmpty()) throw new UserNotFoundException();
        User user = userDB.get();

        Random random = new Random();
        String code = String.format("%04d", random.nextInt(10000));

        user.setPasswordResetCode(new ResetCode(code, LocalDateTime.now().plusMinutes(15)));
        userRepository.save(user);

        Twilio.init(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN);

        String text = "Dear [[name]],\n"
                + "Below you can find your code for changing your password:\n"
                + "[[CODE]]\n"
                + "Have a nice day,\n"
                + "Certificate App.";

        text = text.replace("[[name]]", user.getFirstName());
        text = text.replace("[[CODE]]", user.getPasswordResetCode().getCode());

        Message.creator(new PhoneNumber(phone),
                new PhoneNumber("+13184966544"), text).create();
    }
    private void sendByEmail(String email) throws UserNotFoundException, IOException {
        Optional<User> userDB = userRepository.findByEmail(email);
        if (userDB.isEmpty())
        {
            throw new UserNotFoundException();
        } else {
            Random random = new Random();
            String code = String.format("%04d", random.nextInt(10000));
            User user = userDB.get();
            user.setPasswordResetCode(new ResetCode(code, LocalDateTime.now().plusMinutes(15)));
            userRepository.save(user);

            sendPasswordResetEmail(user);
        }
    }

    private String sendPasswordResetEmail(User user) throws IOException {
        String emailBody = "Dear [[name]],\n"
                + "Below you can find your code for changing your password:\n"
                + "[[CODE]]\n"
                + "Have a nice day,\n"
                + "Certificate App.";

        emailBody = emailBody.replace("[[name]]", user.getFirstName());
        emailBody = emailBody.replace("[[CODE]]", user.getPasswordResetCode().getCode());

        Email from = new Email(SENDER_EMAIL);
        String subject = "Password Reset Code";
        Email to = new Email(user.getEmail());
        Content content = new Content("text/plain", emailBody);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(SENDGRID_API_KEY);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            return response.getBody();
        } catch (IOException ex) {
            throw ex;
        }
    }

    private String sendRegistrationEmail(User user) throws IOException {
        Random random = new Random();
        String code = String.format("%04d", random.nextInt(10000));
        user.setPasswordResetCode(new ResetCode(code, LocalDateTime.now().plusMinutes(15)));
        userRepository.save(user);

        String emailBody = "Dear [[name]],\n"
                + "Below you can find your code for confirmation of your account:\n"
                + "[[CODE]]\n"
                + "Have a nice day,\n"
                + "Certificate App.";

        emailBody = emailBody.replace("[[name]]", user.getFirstName());
        emailBody = emailBody.replace("[[CODE]]", user.getPasswordResetCode().getCode());

        Email from = new Email(SENDER_EMAIL);
        String subject = "CertifyHub account";
        Email to = new Email(user.getEmail());
        Content content = new Content("text/plain", emailBody);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(SENDGRID_API_KEY);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            return response.getBody();
        } catch (IOException ex) {
            throw ex;
    }
    }

    private boolean verifyPassword(String username, String password) throws NoSuchAlgorithmException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty()) {
            return false;
        }
        String passwordDigest = user.get().getPassword();
        return passwordEncoder.matches(password, passwordDigest);
    }

    private void validateRegistration(CreateUserDTO createUserDTO) {
        if(!this.isPhoneNumberValid(createUserDTO.getPhoneNumber())) throw new CustomException("Invalid phone number!");
        if(!this.isEmailValid(createUserDTO.getEmail())) throw new CustomException("Invalid email!");
        if(!this.isPasswordValid(createUserDTO.getPassword(), createUserDTO.getRepeatedPassword())) throw new CustomException("Invalid password!");
    }

    private boolean isPasswordValid(String password, String repeatedPassword) {
        if(!Objects.equals(password, repeatedPassword)) return false;
        String passwordPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private boolean isEmailValid(String email) {
        if(this.userRepository.findByEmail(email).isPresent()) return false;
        String mailPattern = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern pattern = Pattern.compile(mailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        if(this.userRepository.findByPhoneNumber(phoneNumber).isPresent()) return false;
        String phonePattern = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$";
        Pattern pattern = Pattern.compile(phonePattern);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    private boolean isEmail(String email) {
        String mailPattern = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern pattern = Pattern.compile(mailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isPhoneNumber(String phoneNumber) {
        String phonePattern = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$";
        Pattern pattern = Pattern.compile(phonePattern);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
