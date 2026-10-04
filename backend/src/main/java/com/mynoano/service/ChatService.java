package com.mynoano.service;

import com.mynoano.dto.Dtos.*;
import com.mynoano.entity.*;
import com.mynoano.exception.ApiException;
import com.mynoano.repository.*;
import com.mynoano.util.Geo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final MessageRepository msgs;
    private final TeaPostRepository teaRepo;
    private final EventRepository eventRepo;
    private final Access access;
    private final ViewFactory views;

    private MessageView view(Long me, Message m) {
        Object shared = m.getSharedType() == SharedType.NONE ? null : views.sharedView(me, m.getSharedType(), m.getSharedId());
        return new MessageView(m.getId(), m.getSenderId(), m.getBody(), m.getSharedType(), m.getSharedId(), shared, m.getCreatedAt(), m.getSenderId().equals(me));
    }

    @Transactional
    public MessageView send(Long me, Long to, SendMessage r) {
        if (access.blocked(me, to) || !access.friends(me, to)) throw ApiException.forbidden("You can only message friends");
        SharedType st = r.sharedType() == null ? SharedType.NONE : r.sharedType();
        String body = r.body() == null ? "" : r.body().trim();
        if (st == SharedType.NONE && body.isEmpty()) throw ApiException.bad("Message is empty");
        if (st != SharedType.NONE) checkShareable(me, to, st, r.sharedId());
        return view(me, msgs.save(Message.builder().senderId(me).recipientId(to).body(body).sharedType(st).sharedId(r.sharedId()).build()));
    }

    /** ANO items can only be shared with friends who are in your ANO circle. */
    private void checkShareable(Long me, Long to, SharedType st, Long id) {
        if (id == null) throw ApiException.bad("Nothing to share");
        boolean ano;
        if (st == SharedType.EVENT) {
            Event e = eventRepo.findById(id).orElseThrow(() -> ApiException.notFound("Event not found"));
            ano = e.getVisibility() == Visibility.ANO;
            if (ano && !e.getHostId().equals(me) && !access.anoCircle(me, e.getHostId())) throw ApiException.forbidden("You cannot share this event");
        } else {
            TeaPost t = teaRepo.findById(id).orElseThrow(() -> ApiException.notFound("Post not found"));
            ano = t.getSource() == TeaSource.ANO;
            if (ano && !t.getAuthorId().equals(me) && !access.anoCircle(me, t.getAuthorId())) throw ApiException.forbidden("You cannot share this post");
        }
        if (ano && !access.anoCircle(me, to)) throw ApiException.forbidden("ANO items can only go to your ANO circle");
    }

    public List<ConversationView> conversations(Long me) {
        Set<Long> blocked = access.blockedIds(me);
        Map<Long, Message> last = new LinkedHashMap<>();
        Map<Long, Long> unread = new HashMap<>();
        for (Message m : msgs.mine(me)) {
            Long other = m.getSenderId().equals(me) ? m.getRecipientId() : m.getSenderId();
            if (blocked.contains(other)) continue;
            last.putIfAbsent(other, m);
            if (m.getRecipientId().equals(me) && !m.isSeen()) unread.merge(other, 1L, Long::sum);
        }
        return last.entrySet().stream().map(e -> new ConversationView(views.card(me, e.getKey()), view(me, e.getValue()), unread.getOrDefault(e.getKey(), 0L))).toList();
    }

    @Transactional
    public List<MessageView> thread(Long me, Long other) {
        if (access.blocked(me, other)) throw ApiException.notFound("Conversation not found");
        List<Message> list = msgs.thread(me, other);
        list.stream().filter(m -> m.getRecipientId().equals(me) && !m.isSeen()).forEach(m -> { m.setSeen(true); msgs.save(m); });
        return list.stream().map(m -> view(me, m)).toList();
    }
}
