import com.wine.to.up.winelab.parser.service.services.ParserService;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import org.junit.Test;
import org.junit.Assert;

import java.io.IOException;

public class Test2 {
    @Test
    public void testParser() {

        try {
            ParserService parserService = new ParserService();
            Wine wine = parserService.parseProduct(1009581);


            Assert.assertNotNull(wine.getName());           //test the fields are not null/null (depends on the field)
            Assert.assertNotNull(wine.getLink());
            Assert.assertNotNull(wine.getImage());
            Assert.assertNotNull(wine.getManufacturer());
            Assert.assertNotNull(wine.getBrand());
            Assert.assertNotNull(wine.getCountry());
            Assert.assertNull(wine.getRegion());
            Assert.assertNotNull(wine.getDescription());


        } catch (Exception e) {

            System.out.println(e.getMessage());

        }

    }
}