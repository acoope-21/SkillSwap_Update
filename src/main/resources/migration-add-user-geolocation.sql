-- Migration: Add geolocation fields to users table
-- This migration adds latitude, longitude, and showLocation fields to support location-based matching at the user level

ALTER TABLE users 
ADD COLUMN IF NOT EXISTS latitude DECIMAL(10, 8),
ADD COLUMN IF NOT EXISTS longitude DECIMAL(11, 8),
ADD COLUMN IF NOT EXISTS show_location BOOLEAN DEFAULT FALSE;

-- Add index for location-based queries
CREATE INDEX IF NOT EXISTS idx_users_location ON users(latitude, longitude) WHERE show_location = TRUE;

