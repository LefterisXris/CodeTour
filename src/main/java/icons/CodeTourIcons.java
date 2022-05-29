package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * @author Eleftherios Chrysochoidis
 * Date: 26/5/2022
 */
public interface CodeTourIcons {
   /**
    * 40x40
    */
   Icon LOGO = IconLoader.getIcon("/codetour-logo.svg", CodeTourIcons.class);
   /**
    * 13x13
    */
   Icon LOGO_XS = IconLoader.getIcon("/codetour-logo-xs.svg", CodeTourIcons.class);
   /**
    * 16x16
    */
   Icon LOGO_S = IconLoader.getIcon("/codetour-log-s.svg", CodeTourIcons.class);
   /**
    * 32x32
    */
   Icon LOGO_M = IconLoader.getIcon("/codetour-logo-m.svg", CodeTourIcons.class);
   /**
    * 13x13
    */
   Icon STEP = IconLoader.getIcon("/step.svg", CodeTourIcons.class);

}