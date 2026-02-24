package com.bxb.sunduk_pay.factories.photoFactory;

import com.bxb.sunduk_pay.exception.InvalidPhotoException;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.validations.Validations;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;


@RequiredArgsConstructor
@Log4j2
@Service
public class ProfilePhotoAsync {

    private final Validations validations;
    private final UserRepository userRepository;

    private final ExecutorService photoExecutor =
            new ThreadPoolExecutor(
                    16,
                    32,
                    60,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(2000)
            );

    public void uploadFromPath(final Path path, final String uuid) {

        CompletableFuture.runAsync(() -> {
            try {
                byte[] imageBytes = Files.readAllBytes(path);

                User user = validations.getUserInfo(uuid);
                user.setProfilePhoto(imageBytes);
                userRepository.save(user);

                Files.deleteIfExists(path);

            } catch (Exception e) {
                log.error("Async profile photo upload failed", e);
            }
        }, photoExecutor);
    }

    @PreDestroy
    public void shutdown() {
        photoExecutor.shutdown();
    }
}