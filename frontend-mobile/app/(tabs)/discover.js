import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Dimensions,
  Animated,
  PanResponder,
  Alert,
  TouchableOpacity,
} from 'react-native';
import { useRouter } from 'expo-router';
import { useAuth } from '../../src/contexts/AuthContext';
import { userAPI, profileAPI, swipeAPI, photoAPI } from '../../src/services/api';
import { theme } from '../../src/styles/theme';
import DiscoverCard from '../../src/components/DiscoverCard';

const { width: SCREEN_WIDTH } = Dimensions.get('window');
const SWIPE_THRESHOLD = 120;

export default function Discover() {
  const [users, setUsers] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({
    years: [],
    locationEnabled: false,
    maxDistance: 50,
  });
  const { getCurrentUserId } = useAuth();
  const router = useRouter();

  useEffect(() => {
    loadUsers();
  }, [filters]);

  const loadUsers = async () => {
    try {
      const currentUserId = getCurrentUserId();
      const [allUsers, allProfiles, swipes] = await Promise.all([
        userAPI.getAll(),
        profileAPI.getAll(),
        swipeAPI.getByUser(currentUserId),
      ]);

      const swipedUserIds = new Set(swipes.map(s => s.swipee?.userId || s.swipee?.id));

      // Merge user and profile data and load photos
      const usersWithProfiles = await Promise.all(
        allUsers.map(async (user) => {
          const profile = allProfiles.find(p => p.user?.userId === user.userId);
          let photoUrl = null;
          
          // Load primary photo if profile exists
          if (profile?.profileId) {
            try {
              const photos = await photoAPI.getByProfile(profile.profileId);
              const primaryPhoto = photos.find(p => p.isPrimary) || photos[0];
              photoUrl = primaryPhoto?.photoUrl;
            } catch (error) {
              console.error(`Error loading photo for user ${user.userId}:`, error);
            }
          }
          
          return {
            ...user,
            profile: profile,
            major: profile?.major || '',
            location: profile?.location || user.location || '',
            latitude: profile?.latitude || user.latitude,
            longitude: profile?.longitude || user.longitude,
            photoUrl: photoUrl,
          };
        })
      );

      let filteredUsers = usersWithProfiles.filter(
        user => user.userId !== currentUserId && !swipedUserIds.has(user.userId)
      );

      // Calculate distances if current user has location
      const currentUser = allUsers.find(u => u.userId === currentUserId);
      const currentProfile = allProfiles.find(p => p.user?.userId === currentUserId);
      const currentLat = currentProfile?.latitude || currentUser?.latitude;
      const currentLon = currentProfile?.longitude || currentUser?.longitude;

      if (currentLat && currentLon) {
        filteredUsers.forEach(user => {
          if (user.latitude && user.longitude) {
            user.distance = calculateDistance(currentLat, currentLon, user.latitude, user.longitude);
          }
        });
      }

      // Apply year filter
      if (filters.years.length > 0) {
        filteredUsers = filteredUsers.filter(user => {
          return user.profile && filters.years.includes(user.profile.year);
        });
      }

      // Apply location filter
      if (filters.locationEnabled) {
        filteredUsers = filteredUsers.filter(user => {
          if (!user.latitude || !user.longitude) return false;
          return user.distance <= filters.maxDistance;
        });
      }

      setUsers(filteredUsers);
      setCurrentIndex(0);
    } catch (error) {
      console.error('Error loading users:', error);
    } finally {
      setLoading(false);
    }
  };

  const calculateDistance = (lat1, lon1, lat2, lon2) => {
    const R = 6371; // Earth's radius in km
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lon2 - lon1) * Math.PI / 180;
    const a = 
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
      Math.sin(dLon / 2) * Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return Math.round(R * c * 10) / 10; // Round to 1 decimal place
  };

  const handleSwipe = async (direction) => {
    if (currentIndex >= users.length) return;

    const currentUser = users[currentIndex];
    const isLike = direction === 'right';
    const swiperId = getCurrentUserId();
    const swipeeId = currentUser.userId;

    // Validate IDs
    if (!swiperId || !swipeeId) {
      console.error('Invalid user IDs:', { swiperId, swipeeId });
      Alert.alert('Error', 'Invalid user information');
      return;
    }

    try {
      // Ensure userIds are numbers, not strings
      const swipeData = {
        swiper: { 
          userId: typeof swiperId === 'string' ? parseInt(swiperId, 10) : swiperId 
        },
        swipee: { 
          userId: typeof swipeeId === 'string' ? parseInt(swipeeId, 10) : swipeeId 
        },
        isLike: isLike,
      };

      // Validate the data before sending
      if (!swipeData.swiper.userId || !swipeData.swipee.userId) {
        throw new Error('Invalid user IDs in swipe data');
      }

      console.log('Sending swipe data:', JSON.stringify(swipeData, null, 2));
      const result = await swipeAPI.create(swipeData);
      console.log('Swipe successful:', result);

      // Update state: remove swiped user and reset to show next card
      const newIndex = currentIndex + 1;
      setUsers(prevUsers => {
        // Remove the current user from the list
        return prevUsers.filter((_, index) => index !== currentIndex);
      });
      
      // Set index to 0 to show the next user (which is now at index 0 after removal)
      // Use setTimeout to ensure state updates complete before updating index
      setTimeout(() => {
        setCurrentIndex(0);
      }, 0);
    } catch (error) {
      console.error('Error performing swipe:', error);
      console.error('Error details:', {
        message: error.message,
        response: error.response?.data,
        status: error.response?.status,
        swipeData: { swiperId, swipeeId, isLike },
      });
      
      // Show user-friendly error message
      const errorMessage = error.response?.data?.message 
        || error.response?.data 
        || error.message 
        || 'Failed to perform swipe. Please try again.';
      
      Alert.alert('Swipe Error', errorMessage);
    }
  };

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <Text style={styles.loadingText}>Loading users...</Text>
      </View>
    );
  }

  if (currentIndex >= users.length) {
    return (
      <View style={styles.emptyContainer}>
        <Text style={styles.emptyTitle}>No more users!</Text>
        <Text style={styles.emptyText}>Check back later for new profiles.</Text>
      </View>
    );
  }

  const currentUser = users[currentIndex];

  const toggleYearFilter = (year) => {
    setFilters(prev => ({
      ...prev,
      years: prev.years.includes(year)
        ? prev.years.filter(y => y !== year)
        : [...prev.years, year],
    }));
  };

  const toggleLocationFilter = () => {
    setFilters(prev => ({
      ...prev,
      locationEnabled: !prev.locationEnabled,
    }));
  };

  return (
    <View style={styles.container}>
      {/* Filter Section */}
      <View style={styles.filterContainer}>
        <Text style={styles.filterTitle}>Filters</Text>
        
        <View style={styles.filterSection}>
          <Text style={styles.filterLabel}>Year</Text>
          <View style={styles.filterPills}>
            {['Freshman', 'Sophomore', 'Junior', 'Senior', 'Graduate', 'Faculty'].map(year => (
              <TouchableOpacity
                key={year}
                style={[
                  styles.filterPill,
                  filters.years.includes(year) && styles.filterPillActive
                ]}
                onPress={() => toggleYearFilter(year)}
              >
                <Text style={[
                  styles.filterPillText,
                  filters.years.includes(year) && styles.filterPillTextActive
                ]}>
                  {year}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>

        <View style={styles.filterSection}>
          <View style={styles.filterCheckboxRow}>
            <TouchableOpacity
              style={styles.filterCheckbox}
              onPress={toggleLocationFilter}
            >
              <Text style={styles.filterCheckboxText}>
                {filters.locationEnabled ? '☑' : '☐'} Show nearby users only
              </Text>
            </TouchableOpacity>
          </View>
          {filters.locationEnabled && (
            <View style={styles.filterRange}>
              <Text style={styles.filterRangeLabel}>
                Max distance: {filters.maxDistance} km
              </Text>
              <View style={styles.sliderContainer}>
                <Text style={styles.sliderLabel}>5 km</Text>
                <View style={styles.sliderTrack}>
                  <View style={[styles.sliderFill, { width: `${((filters.maxDistance - 5) / 195) * 100}%` }]} />
                </View>
                <Text style={styles.sliderLabel}>200 km</Text>
              </View>
              <View style={styles.sliderButtons}>
                {[10, 25, 50, 100, 200].map(dist => (
                  <TouchableOpacity
                    key={dist}
                    style={[
                      styles.sliderButton,
                      filters.maxDistance === dist && styles.sliderButtonActive
                    ]}
                    onPress={() => setFilters(prev => ({ ...prev, maxDistance: dist }))}
                  >
                    <Text style={[
                      styles.sliderButtonText,
                      filters.maxDistance === dist && styles.sliderButtonTextActive
                    ]}>
                      {dist}
                    </Text>
                  </TouchableOpacity>
                ))}
              </View>
            </View>
          )}
        </View>
      </View>

      {/* Card */}
      <View style={styles.cardContainer}>
        <DiscoverCard
          key={currentUser.userId}
          user={currentUser}
          onSwipe={handleSwipe}
          onViewProfile={() => router.push(`/profile/${currentUser.userId}`)}
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.bgPrimary,
    padding: theme.spacing.md,
  },
  filterContainer: {
    backgroundColor: theme.colors.bgCard,
    borderRadius: theme.borderRadius.lg,
    padding: theme.spacing.md,
    marginBottom: theme.spacing.md,
    borderWidth: 1,
    borderColor: theme.colors.borderColor,
  },
  filterTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: theme.colors.textPrimary,
    marginBottom: theme.spacing.md,
  },
  filterSection: {
    marginBottom: theme.spacing.md,
  },
  filterLabel: {
    fontSize: 14,
    fontWeight: '500',
    color: theme.colors.textSecondary,
    marginBottom: theme.spacing.sm,
  },
  filterPills: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: theme.spacing.sm,
  },
  filterPill: {
    paddingHorizontal: theme.spacing.md,
    paddingVertical: theme.spacing.sm,
    borderRadius: theme.borderRadius.md,
    backgroundColor: theme.colors.bgSecondary,
    borderWidth: 1,
    borderColor: theme.colors.borderColor,
  },
  filterPillActive: {
    backgroundColor: theme.colors.accentPrimary,
    borderColor: theme.colors.accentPrimary,
  },
  filterPillText: {
    fontSize: 12,
    color: theme.colors.textSecondary,
    fontWeight: '500',
  },
  filterPillTextActive: {
    color: '#fff',
    fontWeight: '600',
  },
  filterCheckboxRow: {
    marginBottom: theme.spacing.sm,
  },
  filterCheckbox: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  filterCheckboxText: {
    fontSize: 14,
    color: theme.colors.textPrimary,
  },
  filterRange: {
    marginTop: theme.spacing.sm,
  },
  filterRangeLabel: {
    fontSize: 14,
    color: theme.colors.textSecondary,
    marginBottom: theme.spacing.sm,
  },
  sliderContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
    marginBottom: theme.spacing.sm,
  },
  sliderTrack: {
    flex: 1,
    height: 4,
    backgroundColor: theme.colors.bgSecondary,
    borderRadius: 2,
    position: 'relative',
  },
  sliderFill: {
    height: '100%',
    backgroundColor: theme.colors.accentPrimary,
    borderRadius: 2,
  },
  sliderLabel: {
    fontSize: 10,
    color: theme.colors.textMuted,
  },
  sliderButtons: {
    flexDirection: 'row',
    gap: theme.spacing.xs,
    flexWrap: 'wrap',
  },
  sliderButton: {
    paddingHorizontal: theme.spacing.sm,
    paddingVertical: theme.spacing.xs,
    borderRadius: theme.borderRadius.sm,
    backgroundColor: theme.colors.bgSecondary,
    borderWidth: 1,
    borderColor: theme.colors.borderColor,
  },
  sliderButtonActive: {
    backgroundColor: theme.colors.accentPrimary,
    borderColor: theme.colors.accentPrimary,
  },
  sliderButtonText: {
    fontSize: 12,
    color: theme.colors.textSecondary,
  },
  sliderButtonTextActive: {
    color: '#fff',
    fontWeight: '600',
  },
  cardContainer: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  loadingContainer: {
    flex: 1,
    backgroundColor: theme.colors.bgPrimary,
    alignItems: 'center',
    justifyContent: 'center',
  },
  loadingText: {
    color: theme.colors.textSecondary,
    fontSize: 16,
  },
  emptyContainer: {
    flex: 1,
    backgroundColor: theme.colors.bgPrimary,
    alignItems: 'center',
    justifyContent: 'center',
    padding: theme.spacing.xl,
  },
  emptyTitle: {
    fontSize: 24,
    fontWeight: '700',
    color: theme.colors.textPrimary,
    marginBottom: theme.spacing.md,
  },
  emptyText: {
    fontSize: 16,
    color: theme.colors.textSecondary,
    textAlign: 'center',
  },
});

