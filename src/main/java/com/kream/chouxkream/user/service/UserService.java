package com.kream.chouxkream.user.service;

import com.kream.chouxkream.bid.model.dto.BidStatus;
import com.kream.chouxkream.bid.model.dto.BidType;
import com.kream.chouxkream.bid.model.entity.Bid;
import com.kream.chouxkream.bid.model.entity.Payment;
import com.kream.chouxkream.bid.repository.BidRepository;
import com.kream.chouxkream.product.model.entity.Product;
import com.kream.chouxkream.product.model.entity.ProductImages;
import com.kream.chouxkream.product.model.entity.ProductSize;
import com.kream.chouxkream.role.entity.Role;
import com.kream.chouxkream.role.repository.RoleRepository;
import com.kream.chouxkream.user.model.dto.*;
import com.kream.chouxkream.user.model.entity.Address;
import com.kream.chouxkream.user.model.entity.AuthNumber;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.model.entity.UserRole;
import com.kream.chouxkream.user.repository.AuthNumberRepositroy;
import com.kream.chouxkream.user.repository.UserRepository;
import com.kream.chouxkream.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final AuthNumberRepositroy authNumberRepositroy;
    private final BidRepository bidRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender mailSender;

    @Transactional
    public boolean isEmailExists(String email) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.isPresent();
    }

    @Transactional
    public void join(UserJoinDto userJoinDto) {

        // 회원 저장
        User user = userJoinDto.toEntity();
        user.setActive(true);
        user.encodePassword(bCryptPasswordEncoder);
        userRepository.save(user);

        // 일반 회원 권한 체크
        Optional<Role> optionalRole = roleRepository.findByRoleName("ROLE_USER");
        Role role = optionalRole.get();

        // 회원-권한 저장
        UserRoleKey userRoleKey = new UserRoleKey(user.getUserNo(), role.getRoleId());
        UserRole userRole = new UserRole();
        userRole.setId(userRoleKey);
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);
    }

    @Async
    public void sendAuthEmail(String toEmail) throws MessagingException {

        String authNum = makeAuthNumber();
        String setFrom = "shhwang0930@gmail.com";
        String title = "[CHOUXKREAM]회원가입 인증 이메일";
        String content =
                "CHOUXKREAM을 방문해주셔서 감사합니다." +
                "<br><br>" +
                "인증 번호는 " + authNum + "입니다." +
                "<br>" +
                "인증번호를 제대로 입력해주세요";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");
        helper.setFrom(setFrom);
        helper.setTo(toEmail);
        helper.setSubject(title);
        helper.setText(content,true);
        mailSender.send(message);

        AuthNumber authNumber = new AuthNumber(toEmail, authNum);
        authNumberRepositroy.save(authNumber);
    }

    public String makeAuthNumber() {

        Random r = new Random();
        StringBuilder randomNumber = new StringBuilder();
        for(int i = 0; i < 6; i++)
            randomNumber.append(Integer.toString(r.nextInt(10)));

        return randomNumber.toString();
    }

    public boolean checkAuthNumber(String email, String authNum) {

        Optional<AuthNumber> optionalAuthNumber = authNumberRepositroy.findById(email);
        if (optionalAuthNumber.isPresent()) {

            AuthNumber authNumber = optionalAuthNumber.get();
            if (email.equals(authNumber.getEmail()) && authNum.equals(authNumber.getAuthNum())) {

                return true;
            } else {

                return false;
            }
        } else {

            return false;
        }
    }

    @Transactional
    public String findEmailByPhoneNumber(String phoneNumber) {

        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        return optionalUser.map(user -> emailMasking(user.getEmail())).orElse(null);
    }

    private String emailMasking(String email) {

        int pos = email.indexOf('@');
        String id = email.substring(0, pos);

        // 아이디 앞 2글자를 제외한 나머지를 *로 마스킹
        return id.substring(0, Math.min(id.length(), 2)) +
                email.substring(2, pos).replaceAll(".", "*") +  // 정규 표현식에서 .은 임의의 문자 하나를 의미
                email.substring(pos);
    }

    @Transactional
    public Optional<User> findByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    public UserInfoDto getUserInfo(String email) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {

            return null;
        } else {

            return new UserInfoDto(optionalUser.get());
        }
    }

    @Transactional
    public void updateEmail(String email, String updateEmail) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {

            User user = optionalUser.get();
            user.setEmail(updateEmail);
            userRepository.save(user);
        }
    }

    @Transactional
    public void updateName(String email, String updateName) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {

            User user = optionalUser.get();
            user.setUsername(updateName);
            userRepository.save(user);
        }
    }

    @Transactional
    public boolean isNicknameExists(String updateNickname) {
        Optional<User> optionalUser = userRepository.findByNickname(updateNickname);
        return optionalUser.isPresent();
    }

    @Transactional
    public void updateNickname(String email, String updateNickname) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {

            User user = optionalUser.get();
            user.setNickname(updateNickname);
            userRepository.save(user);
        }
    }

    @Transactional
    public void updateUserDesc(String email, String updateUserDesc) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {

            User user = optionalUser.get();
            user.setUserDesc(updateUserDesc);
            userRepository.save(user);
        }
    }

    @Transactional
    public boolean checkMatchingEmailAndPhoneNumber(String email, String phoneNumber) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {

            return optionalUser.get().getPhoneNumber().equals(phoneNumber);
        }
        return false;
    }

    @Async
    @Transactional
    public void sendTempPasswordEmail(String email) throws MessagingException {

        String tempPassword = generateTempPassword();
        String setFrom = "shhwang0930@gmail.com"; // email-config에 설정한 자신의 이메일 주소를 입력
        String toMail = email;
        String title = "[CHOUXKREAM]임시 비밀번호 발급 이메일";
        String content =
                "임시 발급된 패스워드 입니다. " +
                        "<br><br>" +
                        "인증 번호는 " + tempPassword + "입니다." +
                        "<br>" +
                        "인증번호를 제대로 입력해주세요";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");//이메일 메시지와 관련된 설정을 수행합니다.
        // true를 전달하여 multipart 형식의 메시지를 지원하고, "utf-8"을 전달하여 문자 인코딩을 설정
        helper.setFrom(setFrom);//이메일의 발신자 주소 설정
        helper.setTo(toMail);//이메일의 수신자 주소 설정
        helper.setSubject(title);//이메일의 제목을 설정
        helper.setText(content,true);//이메일의 내용 설정 두 번째 매개 변수에 true를 설정하여 html 설정으로한다.
        mailSender.send(message);

        User user = userRepository.findByEmail(email).get();
        user.setPassword(tempPassword);
        user.encodePassword(bCryptPasswordEncoder);
        System.out.println("tempPassword = " + tempPassword);

        userRepository.save(user);
    }

    public String generateTempPassword() {

        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = upper.toLowerCase();
        String digits = "0123456789";
        String special_chars = "@$!%*#?&";
        String all_chars = upper + lower + digits + special_chars;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // 최소 8자의 임시 비밀번호 생성
        int length = 8;

        // 적어도 하나의 영문자, 숫자, 특수문자 를 추가
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special_chars.charAt(random.nextInt(special_chars.length())));
        // 나머지 문자 추가
        for (int i = 0; i < length - 3; i++) {
            password.append(all_chars.charAt(random.nextInt(all_chars.length())));
        }

        // 임시 비밀번호를 무작위로 섞음
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(length);
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(randomIndex));
            password.setCharAt(randomIndex, temp);
        }

        return password.toString();
    }

    @Transactional
    public boolean checkPassword(String email, String password) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {

            return false;
        }
        return bCryptPasswordEncoder.matches(password, optionalUser.get().getPassword());
    }

    @Transactional
    public void updatePassword(String email, String updatePassword) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {

            User user = optionalUser.get();
            user.setPassword(updatePassword);
            user.encodePassword(bCryptPasswordEncoder);
            userRepository.save(user);
        }
    }

    public int getMyPoints(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("not found user"));
        return user.getPoint();
    }

    @Transactional
    public void DeActivateUser(String email) {
        User user = userRepository.findByEmail(email)
                        .orElseThrow(()->new RuntimeException("not found user"));
        user.deActivate();
        userRepository.save(user);
    }
    @Transactional
    public void ActivateUser(String email) {
        User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("not found user"));
        user.Activate();
        userRepository.save(user);
    }

    @Transactional
    public Page<Bid> getPagedBuyBidByUserNo(Long userNo, LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {

        Date startDateTime = toDate(startDate.atStartOfDay());
        Date endDateTime = toDate(endDate.atStartOfDay().plusDays(1));
        return bidRepository.findByUserUserNoAndBidTypeAndBidStatusNotAndCreateDateBetween(userNo, BidType.buy, BidStatus.bid_delete, startDateTime, endDateTime, pageRequest);
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public List<UserBidDto> setUserBidInfo(Page<Bid> pagingBuyBid) {

        List<UserBidDto> userBuyBidDtoList = new ArrayList<>();
        for (Bid bid : pagingBuyBid) {

            UserBidDto userBuyBidDto = new UserBidDto();
            ProductSize productSize = bid.getProductSize();
            if (productSize == null) {

                continue;
            }

            Product product = productSize.getProduct();
            if (product == null) {

                continue;
            }

            Set<ProductImages> productImagesSet = product.getProductImages();
            Optional<ProductImages> optionalProductImages = productImagesSet.stream().findFirst();
            String imageUrl = null;
            if (optionalProductImages.isPresent()) {
                imageUrl = optionalProductImages.get().getImageUrl();
            }

            userBuyBidDto.setBidInfo(bid);
            userBuyBidDto.setProductInfo(product);
            userBuyBidDto.setProductSizeInfo(productSize);
            userBuyBidDto.setProductImageUrl(imageUrl);
            userBuyBidDtoList.add(userBuyBidDto);
        }

        return userBuyBidDtoList;
    }

    @Transactional
    public Page<Bid> getPagedSellBidByUserNo(Long userNo, LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {

        Date startDateTime = toDate(startDate.atStartOfDay());
        Date endDateTime = toDate(endDate.atStartOfDay().plusDays(1));
        return bidRepository.findByUserUserNoAndBidTypeAndBidStatusNotAndCreateDateBetween(userNo, BidType.sell, BidStatus.bid_delete, startDateTime, endDateTime, pageRequest);
    }

    @Transactional
    public UserBidDetailDto getBidDetailInfo(Long userNo, Long bidNo) {


        Optional<Bid> optionalBid = bidRepository.findByUserUserNoAndBidNo(userNo, bidNo);
        if (optionalBid.isPresent()) {
            Bid bid = optionalBid.get();

            UserBidDetailDto userBidDetailDto = new UserBidDetailDto();

            Payment payment = bid.getPayment();
            ProductSize productSize = bid.getProductSize();
            Product product = productSize.getProduct();

            Set<ProductImages> productImagesSet = product.getProductImages();
            Optional<ProductImages> optionalProductImages = productImagesSet.stream().findFirst();
            String imageUrl = null;
            if (optionalProductImages.isPresent()) {
                imageUrl = optionalProductImages.get().getImageUrl();
            }

            User user = bid.getUser();
            Set<Address> addressSet = user.getAddresses();
            Optional<Address> optionalAddress = addressSet.stream().findFirst();
            Address address = null;
            if (optionalAddress.isEmpty()) {

                return null;
            }
            address = optionalAddress.get();

            userBidDetailDto.setBidInfo(bid);
            userBidDetailDto.setProductInfo(product);
            userBidDetailDto.setProductSizeInfo(productSize);
            userBidDetailDto.setProductImageUrl(imageUrl);
            userBidDetailDto.setAddressInfo(address);
            userBidDetailDto.setPaymentInfo(payment);

            return userBidDetailDto;
        }

        return null;
    }

    @Transactional
    public boolean deleteBid(Long userNo, Long bidNo) {

        Optional<Bid> optionalBid = bidRepository.findByUserUserNoAndBidNo(userNo, bidNo);
        if (optionalBid.isPresent()) {

            optionalBid.get().setBidStatus(BidStatus.bid_delete);
            bidRepository.save(optionalBid.get());
            return true;
        }

        return false;
    }
}
