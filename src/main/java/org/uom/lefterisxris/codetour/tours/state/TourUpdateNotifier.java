package org.uom.lefterisxris.codetour.tours.state;

import com.intellij.util.messages.Topic;
import org.uom.lefterisxris.codetour.tours.domain.Tour;

/**
 * @author Eleftherios Chrysochoidis
 * Date: 8/5/2022
 */
public interface TourUpdateNotifier {

   Topic<TourUpdateNotifier> TOPIC = Topic.create("Tour UI Update", TourUpdateNotifier.class);

   void tourUpdated(Tour tour);
}
