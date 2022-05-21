package org.uom.lefterisxris.codetour.tours.service;

import org.apache.commons.lang3.StringUtils;
import org.uom.lefterisxris.codetour.tours.domain.Props;

/**
 * @author Eleftherios Chrysochoidis
 * Date: 21/5/2022
 */
public class Utils {

   /**
    * Removes whitespaces and transforms the given title in camelCase
    * e.g. Basket Items Issue Reproduce --> basketItemsIssueReproduce.tour
    *
    * @param title The given title
    * @return The suggested filename for this title, in camelCase without whitespaces
    */
   public static String fileNameFromTitle(String title) {
      final StringBuilder sb = new StringBuilder();
      for (int i = 0; i < title.length(); i++) {
         if (StringUtils.isWhitespace(title.charAt(i) + "")) {
            if (i < title.length() - 1 && !StringUtils.isWhitespace(title.charAt(i + 1) + "")) {
               sb.append(StringUtils.capitalize(title.charAt(i + 1) + ""));
               i++; // skip the next
            }
         } else
            sb.append(title.charAt(i));
      }

      return StringUtils.uncapitalize(sb.append(Props.TOUR_EXTENSION_FULL).toString());
   }

}