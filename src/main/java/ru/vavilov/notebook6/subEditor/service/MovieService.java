package ru.vavilov.notebook6.subEditor.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vavilov.notebook6.subEditor.model.Movie;
import ru.vavilov.notebook6.subEditor.model.SubtitleEntry;
import ru.vavilov.notebook6.subEditor.model.Url;
import ru.vavilov.notebook6.subEditor.repository.MovieRepository;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.url}")
    private String minioUrl;

    public Movie saveMovie(MultipartFile file)
        throws IOException, ServerException, InsufficientDataException, ErrorResponseException,
        NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
        XmlParserException, InternalException {

        String originalFilename = Objects.requireNonNull(file.getOriginalFilename(), "Filename is required");
        Movie existingMovie = movieRepository.getMovieByName(originalFilename);

        if (existingMovie == null) {
            return saveNewMovie(file, originalFilename);
        } else {
            return updateSubtitles(existingMovie);
        }
    }

    private Movie saveNewMovie(MultipartFile file, String filename)
        throws IOException, ServerException, InsufficientDataException, ErrorResponseException,
        NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
        XmlParserException, InternalException {

        byte[] fileBytes = file.getBytes();

        try (InputStream uploadStream = new ByteArrayInputStream(fileBytes)) {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(filename)
                    .stream(uploadStream, fileBytes.length, -1)
                    .contentType(file.getContentType())
                    .build()
            );
        }
        String url = String.format("%s/%s/%s", minioUrl, bucket, filename);

        String downloadUrl = minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucket)
                .object(filename)
                .extraQueryParams(Map.of("response-content-disposition", "attachment"))
                .build()
        );

        List<SubtitleEntry> subtitles;
        try (InputStream parseStream = new ByteArrayInputStream(fileBytes)) {
            subtitles = SubParser.parseASS(parseStream);
        }

        Movie movie = new Movie()
            .setName(filename)
            .setUrls(List.of(new Url().setDownloadUrl(downloadUrl).setLanguage("ru")))
            .setSubtitles(subtitles);

        return movieRepository.save(movie);
    }

    private Movie updateSubtitles(Movie movieExist)
        throws ServerException, InsufficientDataException, ErrorResponseException,
        IOException, NoSuchAlgorithmException, InvalidKeyException,
        InvalidResponseException, XmlParserException, InternalException {

        try (InputStream inputStream = minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(bucket)
                .object(movieExist.getName())
                .build())) {
            List<SubtitleEntry> subtitles = SubParser.parseASS(inputStream);
            return movieExist.setSubtitles(subtitles);
        }
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Movie with id " + id + " not found")
        );
    }

    public List<Movie> getAllMovie() {
        return movieRepository.findAll();
    }
}
