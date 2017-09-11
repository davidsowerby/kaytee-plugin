package uk.q3c.kaytee.plugin

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes

/**
 * Encountered problems with Jackson exporting KayTeeExtension as JSON.  The issue seemed to be something to do with conflicting
 * versions, but could find no way of solving that - so elected to use Gson instead.  To do that we need to exclude anything which
 * is annotation with @JsonIgnore
 *
 * Created by David Sowerby on 10 Sep 2017
 */
class JacksonAnnotationExclusionStrategy implements ExclusionStrategy {
    boolean shouldSkipClass(Class<?> clazz) {
        return clazz.getAnnotation(JsonIgnore.class) != null
    }

    boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(JsonIgnore.class) != null
    }
}
