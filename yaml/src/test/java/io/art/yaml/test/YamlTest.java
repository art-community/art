package io.art.yaml.test;


import io.art.meta.test.*;
import io.art.meta.test.meta.*;
import io.art.yaml.descriptor.*;
import org.junit.jupiter.api.*;
import static io.art.core.initializer.ContextInitializer.*;
import static io.art.meta.model.TypedObject.*;
import static io.art.meta.module.MetaActivator.*;
import static io.art.meta.module.MetaModule.*;
import static io.art.meta.test.TestingMetaModelGenerator.*;
import static io.art.yaml.module.YamlActivator.*;
import static io.art.yaml.module.YamlModule.*;

public class YamlTest {
    @BeforeAll
    public static void setup() {
        initialize(meta(MetaMetaTest::new), yaml());
    }

    @Test
    public void testYaml() {
        YamlWriter writer = yamlModule().configuration().getWriter();
        YamlReader reader = yamlModule().configuration().getReader();
        TestingMetaModel model = generateTestingModel();
        String yaml = writer.writeToString(typed(declaration(TestingMetaModel.class).definition(), model));
        TestingMetaModel parsed = reader.read(declaration(TestingMetaModel.class).definition(), yaml);
        parsed.assertEquals(model);
    }
}
