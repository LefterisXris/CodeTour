package org.uom.lefterisxris.codetour.tours.service;

import com.intellij.lang.documentation.DocumentationMarkup;
import org.apache.commons.lang3.StringUtils;
import org.intellij.markdown.ast.ASTNode;
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor;
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor;
import org.intellij.markdown.html.HtmlGenerator;
import org.intellij.markdown.parser.MarkdownParser;
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

   public static boolean equalStr(String s1, String s2) {
      if (s1 == null && s2 == null) return true;
      return getOrDef(s1, "").equals(getOrDef(s2, ""));
   }

   public static boolean equalInt(Integer i1, Integer i2) {
      if (i1 == null && i2 == null) return true;
      return getOrDef(i1, Integer.MIN_VALUE).equals(getOrDef(i2, Integer.MIN_VALUE));
   }

   public static String renderFullDoc(String title, String description, String file) {
      StringBuilder sb = new StringBuilder();
      sb.append(DocumentationMarkup.DEFINITION_START);
      sb.append(String.format("Description of Step '%s'", title));
      sb.append(DocumentationMarkup.DEFINITION_END);
      sb.append(DocumentationMarkup.CONTENT_START);
      sb.append(mdToHtml(description));
      sb.append(DocumentationMarkup.CONTENT_END);
      if (StringUtils.isNotEmpty(file)) {
         sb.append(DocumentationMarkup.SECTIONS_START);
         addKeyValueSection("File:", file, sb);
         sb.append(DocumentationMarkup.SECTIONS_END);
      }
      return sb.toString();
   }

   private static String mdToHtml(String markdown) {
      final MarkdownFlavourDescriptor flavour = new GFMFlavourDescriptor();
      final ASTNode parsedTree = new MarkdownParser(flavour).buildMarkdownTreeFromString(markdown);
      return new HtmlGenerator(markdown, parsedTree, flavour, false).generateHtml();
   }

   private static void addKeyValueSection(String key, String value, StringBuilder sb) {
      sb.append(DocumentationMarkup.SECTION_HEADER_START);
      sb.append(key);
      sb.append(DocumentationMarkup.SECTION_SEPARATOR);
      sb.append("<p>");
      sb.append(value);
      sb.append(DocumentationMarkup.SECTION_END);
   }

   /**
    * normalize (remove nulls)
    */
   private static <T> T getOrDef(T s, T def) {return s != null ? s : def;}


}