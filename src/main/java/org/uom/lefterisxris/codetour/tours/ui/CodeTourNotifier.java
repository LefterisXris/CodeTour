package org.uom.lefterisxris.codetour.tours.ui;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

/**
 * Notifier class to show a notification to the user
 *
 * @author Eleftherios Chrysochoidis
 * Date: 18/4/2022
 */
public class CodeTourNotifier {

   public static void notifyStepDescription(@Nullable Project project, String content) {
      NotificationGroupManager.getInstance()
            .getNotificationGroup("CodeTour Notification")
            .createNotification(content, NotificationType.INFORMATION)
            .notify(project);
   }

}
