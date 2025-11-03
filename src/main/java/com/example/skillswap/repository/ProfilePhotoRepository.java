package com.example.skillswap.repository;

import com.example.skillswap.model.ProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProfilePhotoRepository extends JpaRepository<ProfilePhoto, Long> {
    List<ProfilePhoto> findByProfile_ProfileId(Long profileId);
}
