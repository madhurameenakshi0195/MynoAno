package com.mynoano.dto;

import com.mynoano.entity.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.List;
import java.util.Set;

/** All request/response shapes. Java records keep every DTO to a single line. */
public final class Dtos {
    private Dtos() {}

    // auth
    public record RegisterRequest(@Email @NotBlank String email, @NotBlank @Pattern(regexp = "\\+?[0-9 \\-]{10,18}") String phone, @NotBlank @Size(min = 8, max = 72) String password) {}
    public record VerifyRequest(@NotBlank String email, @NotBlank String code) {}
    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}
    public record EmailRequest(@NotBlank String email) {}
    public record AuthResponse(String token, Long userId, boolean profileComplete) {}

    // profile and discovery
    public record ProfileRequest(@NotBlank @Size(max = 40) String displayName, @Size(max = 500) String bio, Set<String> interests, Integer photoStyle) {}
    public record IntentRequest(String intent) {}
    public record LocationRequest(@NotNull Double lat, @NotNull Double lng) {}
    public record DiscoveryRequest(boolean on, GhostMode ghostMode, Integer offAfterMinutes) {}
    public record PersonCard(Long userId, String displayName, String bio, Set<String> interests, int photoStyle, String intent,
                             boolean selfieVerified, boolean campusVerified, String campus, List<String> commonInterests,
                             int mutualFriends, String friendStatus, boolean intentMatch) {}
    public record PersonProfile(PersonCard card, List<TeaView> publicPosts, List<EventCard> publicEvents, int friendCount) {}
    public record MeResponse(Long userId, String email, PersonCard card, boolean profileComplete, boolean discoveryOn, GhostMode ghostMode, Instant discoveryOffAt) {}

    // friends
    public record FriendView(PersonCard card, String anoStatus, boolean iRequestedAno) {}
    public record WaveRequest(@NotBlank @Size(max = 200) String text) {}
    public record WaveView(Long id, PersonCard from, String text, Instant createdAt) {}
    public record AnoHome(List<FriendView> friends, List<TeaView> tea, List<EventCard> events) {}

    // chat
    public record SendMessage(@Size(max = 2000) String body, SharedType sharedType, Long sharedId) {}
    public record MessageView(Long id, Long senderId, String body, SharedType sharedType, Long sharedId, Object shared, Instant createdAt, boolean mine) {}
    public record ConversationView(PersonCard friend, MessageView last, long unread) {}

    // events
    public record EventRequest(@NotBlank String title, @NotBlank String place, @NotBlank String venueGroup, String venueType,
                               @NotNull Double lat, @NotNull Double lng, Instant startsAt, @NotNull Instant endsAt,
                               Visibility visibility, @Size(max = 2000) String description, List<String> highlights) {}
    public record EventCard(Long id, String title, String place, String venueGroup, String venueType, Instant startsAt, Instant endsAt,
                            boolean live, String hostName, Long hostId, int going, Double distanceMiles, Visibility visibility, boolean joined) {}
    public record PhotoView(Long id, String url, String caption) {}
    public record EventDetail(EventCard card, String description, List<String> highlights, int hereNow, boolean checkedIn, boolean chatOpen, List<PhotoView> photos) {}
    public record VenueGroup(String venueGroup, String venueType, double lat, double lng, int totalGoing, List<EventCard> events) {}
    public record EventChatPost(@NotBlank @Size(max = 1000) String body) {}
    public record EventChatView(Long id, Long senderId, String senderName, String body, Instant createdAt) {}

    // tea
    public record TeaRequest(@NotBlank @Size(max = 1000) String body, @NotNull TeaSource source, boolean nameless, Long eventId) {}
    public record TeaView(Long id, String authorName, Long authorId, boolean nameless, TeaSource source, String body, Long eventId,
                          long spilled, long same, boolean iSpilled, boolean iSame, Instant expiresAt, boolean mine) {}
    public record ReactRequest(@NotNull ReactionType type) {}

    // invites
    public record InviteRequest(@NotBlank @Size(max = 140) String text, @NotNull Integer minutes) {}
    public record InviteView(Long id, String authorName, Long authorId, String text, Instant expiresAt, long going, boolean joined, boolean mine) {}

    // safety
    public record BlockedView(Long userId, String displayName) {}
    public record ReportRequest(@NotBlank @Size(max = 500) String reason) {}
    public record ShareStart(@NotNull Long trustedId, @NotNull Integer minutes, Double lat, Double lng) {}
    public record ShareLocation(@NotNull Double lat, @NotNull Double lng) {}
    public record ShareView(Long ownerId, String ownerName, Double lat, Double lng, Instant expiresAt) {}
}
