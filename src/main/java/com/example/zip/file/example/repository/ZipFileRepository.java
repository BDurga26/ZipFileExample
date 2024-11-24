package com.example.zip.file.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.zip.file.example.entity.ZipFileEntity;


@Repository
public interface ZipFileRepository extends JpaRepository<ZipFileEntity,Long>{

	Optional<ZipFileEntity> findByFileName(String fileName);

}
