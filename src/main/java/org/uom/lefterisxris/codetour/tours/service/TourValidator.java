package org.uom.lefterisxris.codetour.tours.service;

import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.util.NlsSafe;

import java.util.function.Function;

/**
 * @author Eleftherios Chrysochoidis
 * Date: 21/5/2022
 */
public class TourValidator implements InputValidator {

   private final Function<String, Boolean> validator;

   public TourValidator(Function<String, Boolean> validator) {this.validator = validator;}

   @Override
   public boolean checkInput(@NlsSafe String inputString) {
      return validator.apply(inputString);
   }

   @Override
   public boolean canClose(@NlsSafe String inputString) {
      return validator.apply(inputString);
   }
}