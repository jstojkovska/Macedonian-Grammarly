package com.veb.project.veb.service;

import java.util.List;
import java.util.Map;

public interface TextCheckService {
    List<Map<String, String>> checkText(String originalText);
}
