package org.uom.lefterisxris.codetour.tours.service;

import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang3.StringUtils;
import org.intellij.markdown.ast.ASTNode;
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor;
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor;
import org.intellij.markdown.html.HtmlGenerator;
import org.intellij.markdown.parser.MarkdownParser;
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.codetour.tours.domain.Props;
import org.uom.lefterisxris.codetour.tours.domain.Step;

import java.nio.file.Paths;

/**
 * @author Eleftherios Chrysochoidis
 * Date: 21/5/2022
 */
public class Utils {

   /**
    * Custom TagRenderer for md to html, as for some strange reason there is no default implementation now
    * in the related Jetbrains library
    */
   private static final HtmlGenerator.TagRenderer TAG_RENDERER = new HtmlGenerator.TagRenderer() {
      @NotNull
      @Override
      public CharSequence printHtml(@NotNull CharSequence charSequence) {
         return charSequence;
      }

      @NotNull
      @Override
      public CharSequence openTag(@NotNull ASTNode astNode, @NotNull CharSequence charSequence,
                                  @NotNull CharSequence[] charSequences, boolean b) {
         return String.format("<%s>", charSequence);
      }

      @NotNull
      @Override
      public CharSequence closeTag(@NotNull CharSequence charSequence) {
         return String.format("</%s>", charSequence);
      }
   };

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
      sb.append(title);
      sb.append(DocumentationMarkup.DEFINITION_END);
      sb.append(DocumentationMarkup.CONTENT_START);
      if (description != null)
         // For formatting purposes, add <br/> tag when there are 2 consecutive empty lines
         description = description.replaceAll("\\n\\n\\n", "\n\n<br/>\n\n");
      sb.append("\n\n").append(description).append("\n");
      sb.append(DocumentationMarkup.CONTENT_END);
      if (StringUtils.isNotEmpty(file)) {
         sb.append(DocumentationMarkup.SECTIONS_START);
         addKeyValueSection("File:", file, sb);
         sb.append(DocumentationMarkup.SECTIONS_END);
      }
      return mdToHtml(sb.toString());
   }

   public static String mdToHtml(String markdown) {
      final MarkdownFlavourDescriptor flavour = new GFMFlavourDescriptor();
      final ASTNode parsedTree = new MarkdownParser(flavour).buildMarkdownTreeFromString(markdown);
      return new HtmlGenerator(markdown, parsedTree, flavour, false).generateHtml(TAG_RENDERER);
   }

   public static boolean isFileMatchesStep(VirtualFile file, @NotNull Step step) {
      if (file.isDirectory())
         return false;

      final String stepDirectory = step.getDirectory() != null ? step.getDirectory() : "";
      final String stepFilePath = Paths.get(stepDirectory, step.getFile()).toString();
      final String filePath = Paths.get(file.getPath()).toString();

      return filePath.endsWith(stepFilePath);
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