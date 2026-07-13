package com.avnzor.oms_backend.tracking.service;

import com.avnzor.oms_backend.tracking.dto.TrackingListResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrackingService {

    public TrackingListResponse listTracking() {
        return new TrackingListResponse("Tracking placeholder", List.of());
    }
}
