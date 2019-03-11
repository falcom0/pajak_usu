package usu.pajak.util;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.UpdateOperations;
import usu.pajak.model.UserPajak;

import java.io.IOException;
import java.util.List;

public class TestMain {
//    private static MongoClient client = new MongoClient(new MongoClientURI("mongodb://fariz:Laru36Dema@clusterasetmongo-shard-00-00-t3kc1.mongodb.net:27017,clusterasetmongo-shard-00-01-t3kc1.mongodb.net:27017,clusterasetmongo-shard-00-02-t3kc1.mongodb.net:27017/test?ssl=true&replicaSet=ClusterAsetMongo-shard-0&authSource=admin&retryWrites=true")); //connect to mongodb
//private static MongoClient client = new MongoClient(new MongoClientURI("mongodb://fariz:Laru36Dema@172.30.100.75:27017/pajak?authSource=admin")); //connect to mongodb
//    private static Datastore datastore = new Morphia().createDatastore(client, "pajak");

    public static void main(String[] args) throws IOException{
//        final Query<UserPajak> query = datastore.createQuery(UserPajak.class).filter("id_user","832");
//        BasicDBList testLagi = new BasicDBList();
//        BasicDBObject obj = new BasicDBObject();
//        obj.put("halo","come on");
//        testLagi.add(obj);
//        UpdateOperations ops = datastore
//                .createUpdateOperations(UserPajak.class)
//                .push("pendapatan", testLagi);
//        datastore.update(query, (UpdateOperations<Query<UserPajak>>) ops);
//        UserPajak up = new UserPajak();
//        up.setId_user("0");
//        datastore.save(up);
//        new PnsApbn();


    }
    private MongoClient client = new MongoClient(new MongoClientURI("mongodb://fariz:Laru36Dema@clusterasetmongo-shard-00-00-t3kc1.mongodb.net:27017,clusterasetmongo-shard-00-01-t3kc1.mongodb.net:27017,clusterasetmongo-shard-00-02-t3kc1.mongodb.net:27017/test?ssl=true&replicaSet=ClusterAsetMongo-shard-0&authSource=admin&retryWrites=true")); //connect to mongodb
    private Datastore datastore = new Morphia().mapPackage("usu.pajak.model.UserPajak").createDatastore(client, "pajak_2019");

    public TestMain(){
        List<UserPajak> listResult = datastore.createQuery(UserPajak.class).disableValidation()
                .filter("pendapatan.pph21.hasil", ".000").asList();
        UpdateOperations<UserPajak> ops = datastore.createUpdateOperations(UserPajak.class);
        for(UserPajak up : listResult){
            BasicDBList list = up.getPendapatan_tdk_tetap();
            for(Object o:list){
                BasicDBObject test = (BasicDBObject) o;
                ops.set("hasil","0.000");
            }
        }
    }
}
