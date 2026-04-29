package com.archchecker.application;

import com.archchecker.domain.compliance.ViolationReport;

public interface Reporter {
    void render(ViolationReport report);
}
