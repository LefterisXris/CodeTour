package org.uom.lefterisxris.codetour.tours.ui;


import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.impl.NotificationGroupManagerImpl;
import com.intellij.openapi.project.Project;
import org.uom.lefterisxris.codetour.tours.state.StateManager;

public class ToursNotification {

   private NotificationGroup group = new NotificationGroupManagerImpl().getNotificationGroup("CodeTours Notification");

   public void notifyUser(Project project) {
      final String title = "View code toursState?";
      final String content = "Content";
      final Notification notification =
            group.createNotification(title, content, NotificationType.INFORMATION, (notification1, event) -> {
               notification1.expire();
               if (event.getDescription().equals("RunTour")) {
                  new StateManager().getTours(project);
               }
            });
      notification.whenExpired(() -> System.out.println("EXPIRED!!!!"));
      notification.notify(project);
   }
}