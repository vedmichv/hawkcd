/*
 *   Copyright (C) 2016 R&D Solutions Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *
 */

package net.hawkengine.core.utilities.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.hawkengine.model.payload.PublishObject;

import java.lang.reflect.Type;

public class PublishObjectAdapter implements JsonDeserializer<PublishObject> {
    @Override
    public PublishObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//        JsonObject jsonObject = json.getAsJsonObject();
//        JsonArray asJsonArray = json.getAsJsonArray();
//
//        Class classs = PipelineDefinition.class;
//
//        Type listType = new TypeToken<List<PipelineDefinition>>(){}.getType();

        PublishObject deserialize = context.deserialize(json, PublishObject.class);
        return deserialize;
    }
}
