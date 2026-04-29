package com.archchecker.application;

import com.archchecker.domain.profile.StyleProfile;

import java.nio.file.Path;

public interface ProfileRepository {
    StyleProfile load(Path profilePath);
}
