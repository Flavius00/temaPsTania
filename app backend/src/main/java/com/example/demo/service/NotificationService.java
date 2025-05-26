package com.example.demo.service;

import com.example.demo.model.ComercialSpace;
import com.example.demo.model.Notification;
import com.example.demo.model.RentalContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Notify all clients about a new commercial space
     */
    public void notifyNewSpace(ComercialSpace space) {
        Notification notification = new Notification(
                "NEW_SPACE",
                "New commercial space available: " + space.getName(),
                space,
                "all"
        );
        messagingTemplate.convertAndSend("/topic/spaces", notification);
    }

    /**
     * Notify all clients about a space status change
     */
    public void notifySpaceStatusChange(ComercialSpace space) {
        String status = space.getAvailable() ? "available" : "unavailable";
        Notification notification = new Notification(
                "SPACE_STATUS_CHANGE",
                "Space '" + space.getName() + "' is now " + status,
                space,
                "all"
        );
        messagingTemplate.convertAndSend("/topic/spaces", notification);
    }

    /**
     * Notify owner about a new contract for their space
     */
    public void notifyOwnerAboutNewContract(RentalContract contract) {
        if (contract.getSpace() != null && contract.getSpace().getOwner() != null) {
            Long ownerId = contract.getSpace().getOwner().getId();
            Notification notification = new Notification(
                    "NEW_CONTRACT",
                    "New contract for your space: " + contract.getSpace().getName(),
                    contract,
                    ownerId.toString()
            );
            messagingTemplate.convertAndSend("/queue/user." + ownerId, notification);

            // Also send to general topic for admin visibility
            messagingTemplate.convertAndSend("/topic/contracts", notification);
        }
    }

    /**
     * Notify tenant about contract status change
     */
    public void notifyTenantAboutContractChange(RentalContract contract) {
        if (contract.getTenant() != null) {
            Long tenantId = contract.getTenant().getId();
            Notification notification = new Notification(
                    "CONTRACT_UPDATE",
                    "Your contract for " + contract.getSpace().getName() + " has been updated to " + contract.getStatus(),
                    contract,
                    tenantId.toString()
            );
            messagingTemplate.convertAndSend("/queue/user." + tenantId, notification);
        }
    }

    /**
     * Send a direct notification to a specific user
     */
    public void sendDirectNotification(String userId, Notification notification) {
        messagingTemplate.convertAndSend("/queue/user." + userId, notification);
    }

    /**
     * Send a broadcast notification to all users
     */
    public void sendBroadcastNotification(Notification notification) {
        messagingTemplate.convertAndSend("/topic/public", notification);
    }
}