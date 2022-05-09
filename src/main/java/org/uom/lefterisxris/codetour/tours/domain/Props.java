package org.uom.lefterisxris.codetour.tours.domain;

import com.intellij.ui.IconManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Eleftherios Chrysochoidis
 * Date: 9/5/2022
 */
public class Props {

   public static final String TOUR_EXTENSION = "tour";
   public static final String TOUR_EXTENSION_FULL = "." + TOUR_EXTENSION;

   public static final String TOURS_DIR = ".tours";
   public static final Icon ICON = load("icon.png");
   public static final Icon ICON_S = load("icon-small.png");
   public static final Icon ICON_XS = load("icon-xs.png");

   private static @NotNull Icon load(@NotNull String path) {
      return IconManager.getInstance().getIcon(path, Props.class);
   }
}