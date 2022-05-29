package org.uom.lefterisxris.codetour.tours.ui;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import icons.CodeTourIcons;
import org.jetbrains.annotations.Nullable;
import org.uom.lefterisxris.codetour.tours.domain.Tour;

/**
 * Notifier class to show a notification to the user
 *
 * @author Eleftherios Chrysochoidis
 * Date: 18/4/2022
 */
public class CodeTourNotifier {

   private static final Logger LOG = Logger.getInstance(CodeTourNotifier.class);

   public static void notifyStepDescription(@Nullable Project project, String content) {
      NotificationGroupManager.getInstance()
            .getNotificationGroup("CodeTour-Notification")
            .createNotification(content, NotificationType.INFORMATION)
            .setIcon(CodeTourIcons.LOGO_S)
            .notify(project);
      LOG.info("CodeTourNotifier: " + content);
   }

   public static void notifyTourAction(@Nullable Project project, Tour tour, String action, String content) {
      NotificationGroupManager.getInstance()
            .getNotificationGroup("CodeTour-Notification")
            .createNotification("Tour action: " + action, content, NotificationType.INFORMATION)
            .setIcon(CodeTourIcons.LOGO_S)
            .notify(project);
      LOG.info("CodeTourNotifier: " + content);
   }

   public static void warn(@Nullable Project project, String content) {
      NotificationGroupManager.getInstance()
            .getNotificationGroup("CodeTour-Notification")
            .createNotification(content, NotificationType.WARNING)
            .setIcon(CodeTourIcons.LOGO_S)
            .notify(project);
      LOG.warn("CodeTourNotifier: " + content);
   }

   public static void error(@Nullable Project project, String content) {
      NotificationGroupManager.getInstance()
            .getNotificationGroup("CodeTour-Notification")
            .createNotification(content, NotificationType.ERROR)
            .setIcon(CodeTourIcons.LOGO_S)
            .notify(project);
      LOG.error("CodeTourNotifier: " + content);
   }

}
