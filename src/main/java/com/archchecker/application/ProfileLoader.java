package com.archchecker.application;

import com.archchecker.domain.profile.StyleProfile;

import java.nio.file.Path;

public interface ProfileLoader {
    StyleProfile load(Path profilePath);
}
