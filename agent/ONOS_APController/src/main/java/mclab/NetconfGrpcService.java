package mclab;

import java.util.concurrent.ConcurrentMap;

/**
 * Created by mclab on 17. 4. 5.
 */
public interface NetconfGrpcService {

    ConcurrentMap<String, nfcGrpcClient> getaplist();
}
