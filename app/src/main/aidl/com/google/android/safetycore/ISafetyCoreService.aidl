package com.google.android.safetycore;
interface ISafetyCoreService {
    boolean isEnabled();
    void setFeatureEnabled(String featureId, boolean enabled);
    boolean getFeatureStatus(String featureId);
}
