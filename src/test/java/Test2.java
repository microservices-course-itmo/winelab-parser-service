import com.wine.to.up.winelab.parser.service.services.ParserService;
import org.junit.Test;
import org.junit.Assert;

import java.io.IOException;

public class Test2 {
    @Test
    public void testParser() {
        try {
            ParserService parserService = new ParserService();


            Assert.assertNotNull(parserService.parseProduct(1009581).getName());           //test the fields are not null/null (depends on the field)
            Assert.assertNotNull(parserService.parseProduct(1009581).getLink());
            Assert.assertNotNull(parserService.parseProduct(1009581).getImage());
            Assert.assertNotNull(parserService.parseProduct(1009581).getManufacturer());
            Assert.assertNotNull(parserService.parseProduct(1009581).getBrand());
            Assert.assertNotNull(parserService.parseProduct(1009581).getCountry());
            Assert.assertNull(parserService.parseProduct(1009581).getRegion());
            Assert.assertNotNull(parserService.parseProduct(1009581).getDescription());


        } catch (Exception e) {

            System.out.println(e.getMessage());

        }

    }
}