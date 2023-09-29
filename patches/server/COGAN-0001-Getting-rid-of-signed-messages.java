From 0000000000000000000000000000000000000000 Mon Sep 17 00:00:00 2001
From: Cogan Ng <cogan@coganng.com>
Date: Fri, 29 Sep 2023 09:02:20 +0800
Subject: [PATCH] Getting rid of signed messages


diff --git a/src/main/java/net/minecraft/network/chat/SignedMessageChain.java b/src/main/java/net/minecraft/network/chat/SignedMessageChain.java
index c0a80824a0307ea673805015119cc834b268f0dc..8ddb1e61237145084f52b74c4419b9cb6b7a8ffa 100644
--- a/src/main/java/net/minecraft/network/chat/SignedMessageChain.java
+++ b/src/main/java/net/minecraft/network/chat/SignedMessageChain.java
@@ -38,15 +38,7 @@ public class SignedMessageChain {
                 throw new SignedMessageChain.DecodeException(Component.translatable("chat.disabled.expiredProfileKey"), false);
             } else {
                 PlayerChatMessage playerChatMessage = new PlayerChatMessage(signedMessageLink, signature, body, (Component)null, FilterMask.PASS_THROUGH);
-                if (!playerChatMessage.verify(signatureValidator)) {
-                    throw new SignedMessageChain.DecodeException(Component.translatable("multiplayer.disconnect.unsigned_chat"), true, org.bukkit.event.player.PlayerKickEvent.Cause.UNSIGNED_CHAT); // Paper - kick event causes
-                } else {
-                    if (playerChatMessage.hasExpiredServer(Instant.now())) {
-                        LOGGER.warn("Received expired chat: '{}'. Is the client/server system time unsynchronized?", (Object)body.content());
-                    }
-
-                    return playerChatMessage;
-                }
+                return playerChatMessage;
             }
         };
     }
diff --git a/src/main/java/net/minecraft/network/chat/SignedMessageValidator.java b/src/main/java/net/minecraft/network/chat/SignedMessageValidator.java
index 50e7af15ab62f370897125a84d1cf77becbaa211..944bd2ceee5545e66b0b73a8daa2958930346da1 100644
--- a/src/main/java/net/minecraft/network/chat/SignedMessageValidator.java
+++ b/src/main/java/net/minecraft/network/chat/SignedMessageValidator.java
@@ -10,16 +10,10 @@ import org.slf4j.Logger;
 public interface SignedMessageValidator {
     Logger LOGGER = LogUtils.getLogger();
     SignedMessageValidator ACCEPT_UNSIGNED = (message) -> {
-        if (message.hasSignature()) {
-            LOGGER.error("Received chat message with signature from {}, but they have no chat session initialized", (Object)message.sender());
-            return false;
-        } else {
-            return true;
-        }
+        return true;
     };
     SignedMessageValidator REJECT_ALL = (message) -> {
-        LOGGER.error("Received chat message from {}, but they have no chat session initialized and secure chat is enforced", (Object)message.sender());
-        return false;
+        return true;
     };

     boolean updateAndValidate(PlayerChatMessage message);
@@ -37,33 +31,18 @@ public interface SignedMessageValidator {
         }

         private boolean validateChain(PlayerChatMessage message) {
-            if (message.equals(this.lastMessage)) {
-                return true;
-            } else if (this.lastMessage != null && !message.link().isDescendantOf(this.lastMessage.link())) {
-                LOGGER.error("Received out-of-order chat message from {}: expected index > {} for session {}, but was {} for session {}", message.sender(), this.lastMessage.link().index(), this.lastMessage.link().sessionId(), message.link().index(), message.link().sessionId());
-                return false;
-            } else {
-                return true;
-            }
+            return true;
         }

         private boolean validate(PlayerChatMessage message) {
-            if (this.expired.getAsBoolean()) {
-                LOGGER.error("Received message from player with expired profile public key: {}", (Object)message);
-                return false;
-            } else if (!message.verify(this.validator)) {
-                LOGGER.error("Received message with invalid signature from {}", (Object)message.sender());
-                return false;
-            } else {
-                return this.validateChain(message);
-            }
+            return this.validateChain(message);
         }

         @Override
         public boolean updateAndValidate(PlayerChatMessage message) {
             this.isChainValid = this.isChainValid && this.validate(message);
             if (!this.isChainValid) {
-                return false;
+                return true;
             } else {
                 this.lastMessage = message;
                 return true;
