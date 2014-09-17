package wsdemo;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


@Path("bid")
@Produces("application/json")
public class BidRestEndpoint {

  @Inject
  private BidService bidService;

  @POST
  @Path("/place")
  public String place(@FormParam("productId") int productId, @FormParam("userId") String userId) {
    bidService.placeBid(productId, userId);
    return "Bid successfully placed";
  }
}
