package ru.art.platform.api.mapping;

import ru.art.entity.*;
import ru.art.entity.mapper.*;
import ru.art.platform.api.model.*;
import static ru.art.core.checker.CheckerForEmptiness.*;

public interface ProjectMapper {
	String id = "id";

	String name = "name";

	String url = "url";

	ValueToModelMapper<Project, Entity> toProject = entity -> isNotEmpty(entity) ? Project.builder()
			.id(entity.getLong(id))
			.name(entity.getString(name))
			.url(entity.getString(url))
			.build() : Project.builder().build();

	ValueFromModelMapper<Project, Entity> fromProject = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.stringField(name, model.getName())
			.stringField(url, model.getUrl())
			.build() : Entity.entityBuilder().build();
}
