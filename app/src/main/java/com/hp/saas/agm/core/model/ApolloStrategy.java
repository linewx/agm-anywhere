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

/*import com.hp.alm.ali.idea.action.ActionUtil;
import com.hp.alm.ali.idea.content.detail.DetailContent;
import com.hp.alm.ali.idea.content.detail.LinksTableLoader;
import com.hp.alm.ali.idea.content.detail.TableContent;
import com.hp.alm.ali.idea.entity.EntityQuery;
import com.hp.alm.ali.idea.model.parser.RelationList;
import com.hp.alm.ali.idea.rest.RestService;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;*/

public class ApolloStrategy extends MayaStrategy {

    /*private static Map<String, String> developmentAliasMap;
    static  {
        developmentAliasMap = new HashMap<String, String>();
        developmentAliasMap.put("defect", "working-on-defect");
        developmentAliasMap.put("requirement", "connected-to-requirement");
        developmentAliasMap.put("build-instance", "build-instance");
    }

    public ApolloStrategy(Project project, RestService restService) {
        super(project, restService);
    }

    @Override
    public String getDevelopmentAlias(String entity) {
        return developmentAliasMap.get(entity);
    }

    @Override
    public RelationList getRelationList(String entityType) {
        return RelationList.create(restService.getForStream("customization/entities/{0}/relations", entityType));
    }

    @Override
    public List<DetailContent> getDetailContent(Entity entity) {
        List<DetailContent> ret = super.getDetailContent(entity);
        if("requirement".equals(entity.getType())) {
            ret.add(linkedTable(entity));
        }
        return ret;
    }

    @Override
    protected TableContent linkedTable(final Entity entity) {
        EntityQuery linkQuery = new EntityQuery("defect-link");
        if("defect".equals(entity.getType())) {
            linkQuery.setValue("first-endpoint-id", String.valueOf(entity.getId()));
        } else {
            linkQuery.setValue("second-endpoint-id", String.valueOf(entity.getId()));
            linkQuery.setValue("second-endpoint-type", entity.getType());
        }
        ActionToolbar actionToolbar = ActionUtil.createActionToolbar("hpali.defect-link", "detail", true);
        return new TableContent(project, entity, "Links", IconLoader.getIcon("/actions/erDiagram.png"), actionToolbar, new LinksTableLoader(project, entity, linkQuery, linkedTableHiddenFields()));
    }

    @Override
    public List<String> getDefectLinkColumns() {
        return Arrays.asList(
                "first-endpoint-id",
                "second-endpoint-id",
                "second-endpoint-type",
                "second-endpoint-status",
                "second-endpoint-name",
                "comment",
                "link-type");
    }

    @Override
    public boolean hasSecondLevelDefectLink() {
        return false;
    }

    @Override
    public boolean canEditAttachmentFileName() {
        return true;
    }*/
}
