package org.uom.lefterisxris.codetour.tours.state;

import com.intellij.util.messages.Topic;
import org.uom.lefterisxris.codetour.tours.domain.Step;

/**
 * @author Eleftherios Chrysochoidis
 * Date: 8/5/2022
 */
public interface StepSelectionNotifier {

   Topic<StepSelectionNotifier> TOPIC = Topic.create("Select Provided Step", StepSelectionNotifier.class);

   void selectStep(Step step);
}
