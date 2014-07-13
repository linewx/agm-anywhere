/*
 * Copyright 2013 Hewlett-Packard Development Company, L.P
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hp.saas.agm.service;

import com.hp.saas.agm.core.model.parser.EntityList;

public abstract class AbstractCachingEntityService<S> extends AbstractCachingService<S, EntityList, AbstractCachingService.Callback<EntityList>> {

    public AbstractCachingEntityService() {

    }

/*    @Override
    public Entity lookup(final EntityRef ref) {
        synchronized (cache) {
            Entity entity = new Entity(ref.type, ref.id);
            for(EntityList list: cache.values()) {
                int i = list.indexOf(entity);
                if(i >= 0) {
                    return list.get(i);
                }
            }
        }
        return null;
    }*/

/*    @Override
    public void entityLoaded(Entity entity, EntityListener.Event event) {
    }

    @Override
    public void entityNotFound(EntityRef ref, boolean removed) {
    }*/
}
