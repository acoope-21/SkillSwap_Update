-- Migration: Add geolocation fields to profile table
-- This migration adds latitude, longitude, and showLocation fields to support location-based matching

ALTER TABLE profile 
ADD COLUMN IF NOT EXISTS latitude DECIMAL(10, 8),
ADD COLUMN IF NOT EXISTS longitude DECIMAL(11, 8),
ADD COLUMN IF NOT EXISTS show_location BOOLEAN DEFAULT FALSE;

-- Add index for location-based queries
CREATE INDEX IF NOT EXISTS idx_profile_location ON profile(latitude, longitude) WHERE show_location = TRUE;

