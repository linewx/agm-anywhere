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

package com.hp.saas.agm.core.model;

import java.util.ArrayList;
import java.util.List;

public class AliStrategy extends MayaStrategy {

    @Override
    public List<Relation> getRelationList(String entityType) {
        List<Relation> list = super.getRelationList(entityType);

        if("defect".equals(entityType)) {
            list.addAll(relationList("changeset"));
        }

        return list;
    }

    protected List<Relation> relationList(String ... entityTypes) {
        ArrayList<Relation> relations = new ArrayList<Relation>(entityTypes.length);
        for(String entityType: entityTypes) {
            relations.add(new Relation(entityType));
        }
        return relations;
    }

    /*public AliStrategy(Project project, RestService restService) {
        super(project, restService);
    }

    @Override
    public List<Relation> getRelationList(String entityType) {
        List<Relation> list = super.getRelationList(entityType);

        if("defect".equals(entityType)) {
            list.addAll(relationList("changeset"));
        }

        return list;
    }

    @Override
    public List<DetailContent> getDetailContent(Entity entity) {
        List<DetailContent> ret = super.getDetailContent(entity);
        aliStrategyUtil.addCodeChangesDetail(this, entity, ret);
        return ret;
    }*/
}
