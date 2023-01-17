/*
 *  Licensed to GraphHopper GmbH under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for
 *  additional information regarding copyright ownership.
 *
 *  GraphHopper GmbH licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.graphhopper.tools;

import com.graphhopper.GraphHopper;
import com.graphhopper.GraphHopperConfig;
import com.graphhopper.routing.ev.*;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.weighting.custom.CustomProfile;
import com.graphhopper.storage.BaseGraph;
import com.graphhopper.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GraphSpeedMeasurement {

    public static void main(String[] strs) {
        PMap args = PMap.read(strs);
        List<String> result = new ArrayList<>();
        for (int speedBits = 7; speedBits <= 31; speedBits += 3) {
            System.out.println("Running measurement for speedBits=" + speedBits);
            GraphHopperConfig ghConfig = new GraphHopperConfig()
                    .putObject("datareader.file", args.getString("map", "map-matching/files/leipzig_germany.osm.pbf"))
                    .putObject("graph.location", args.getString("location", "graph-speed-measurement") + "-" + speedBits + "-gh")
                    .putObject("graph.dataaccess", args.getString("da", "RAM_STORE"))
                    .putObject("import.osm.ignored_highways", "")
                    .putObject("graph.vehicles", String.format("roads|speed_bits=%d,car|speed_bits=%d,bike|speed_bits=%d,foot|speed_bits=%d", speedBits, speedBits, speedBits, speedBits))
                    .setProfiles(Arrays.asList(
                            new CustomProfile("car").setCustomModel(new CustomModel()).setVehicle("roads")
                    ));
            GraphHopper hopper = new GraphHopper()
                    .init(ghConfig)
                    .importOrLoad();
            BaseGraph baseGraph = hopper.getBaseGraph();

            EncodingManager em = hopper.getEncodingManager();
            EnumEncodedValue<RoadClass> roadClassEnc = em.getEnumEncodedValue(RoadClass.KEY, RoadClass.class);
            DecimalEncodedValue maxSpeedEnc = em.getDecimalEncodedValue(MaxSpeed.KEY);
            DecimalEncodedValue speedEnc = em.getDecimalEncodedValue(VehicleSpeed.key("roads"));
            BooleanEncodedValue accessEnc = em.getBooleanEncodedValue(VehicleAccess.key("roads"));

            EdgeExplorer explorer = baseGraph.createEdgeExplorer();
            Random rnd = new Random(123);

            final int iterations = args.getInt("iters", 10_000_000);
            MiniPerfTest t = new MiniPerfTest().setIterations(iterations)
                    .start((warmup, run) -> {
                        EdgeIterator iter = explorer.setBaseNode(rnd.nextInt(baseGraph.getNodes()));
                        double sum = 0;
                        while (iter.next()) {
                            RoadClass roadClass = iter.get(roadClassEnc);
                            double maxSpeed = iter.get(maxSpeedEnc);
                            double speed = iter.get(speedEnc);
                            boolean access = iter.get(accessEnc);
                            sum += (roadClass == RoadClass.MOTORWAY ? 3 : 1) + (maxSpeed < 100 ? 3 : 1) + (speed > 50 ? 2 : 1) + (access ? 1 : 0);
                        }
                        return (int) sum;
                    });
            result.add(String.format("bits: %d, ints: %d, took: %.2fms, checksum: %d", speedBits, em.getIntsForFlags(), t.getSum(), t.getDummySum()));
            System.out.println(result.get(result.size() - 1));
        }
        System.out.println();
        System.out.println("### RESULT ###");
        for (String res : result)
            System.out.println(res);
    }
}
