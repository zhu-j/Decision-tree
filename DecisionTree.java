

import java.io.Serializable;
import java.util.ArrayList;
import java.text.*;
import java.lang.Math;

public class DecisionTree implements Serializable {

    DTNode rootDTNode;
    int minSizeDatalist; //minimum number of datapoints that should be present in the dataset so as to initiate a split

    // Mention the serialVersionUID explicitly in order to avoid getting errors while deserializing.
    public static final long serialVersionUID = 343L;

    public DecisionTree(ArrayList<Datum> datalist , int min) {
        minSizeDatalist = min;
        rootDTNode = (new DTNode()).fillDTNode(datalist);
       // System.out.println("root: " + printer(rootDTNode));

        System.out.println("root attribute: " + rootDTNode.attribute);
        System.out.println("root threshold: " + rootDTNode.threshold);
        System.out.println("root label: " + rootDTNode.label);
        System.out.println("root leaf: " + rootDTNode.leaf);
    }

    class DTNode implements Serializable {
        //Mention the serialVersionUID explicitly in order to avoid getting errors while deserializing.
        public static final long serialVersionUID = 438L;
        boolean leaf;
        int label = -1;      // only defined if node is a leaf
        int attribute; // only defined if node is not a leaf
        double threshold;  // only defined if node is not a leaf

        DTNode left, right; //the left and right child of a particular node. (null if leaf)

        DTNode() {
            leaf = true;
            threshold = Double.MAX_VALUE;
        }

        /***– leaf: a boolean variable that indicates whether this node object is a leaf or not.
         – label: an integer variable that indicates the label of the node. The label of the node indicates the class of a
         datapoint that reaches that particular leaf node after traversing the tree. This is valid only if the node is a leaf
         node. The classes for this assignment are simply 0 or 1.
         – attribute:Theindexoftheattribute,(i.e.1,...,n)onwhichthedatasetissplitinthatpar- ticular node. The value of the
         attribute is one of {1, . . . , n}. The attribute value is meaningful only for an internal node. The value stored in
         this field is meaningful only for an internal node.
         In the code, the attributes are x[0], x[1], .., x[n-1], hence their indices values are 0, 1, . . . , n − 1, respectively.
         – threshold: This holds the value of the attribute at which the split is done. This is also only meaningful for an internal node.
         – left, right These two variables are of type DTNode, and they represent the two children of an internal node. At the classify stage,
         the left child leads to a decision tree node that handles the case that the value of the attribute is less than the threshold,
         and right handles the case that the value is greater than or equal to the threshold. For a leaf node, they are both null.
         ***/

        // this method takes in a datalist (ArrayList of type datum). It returns the calling DTNode object
        // as the root of a decision tree trained using the datapoints present in the datalist variable and minSizeDatalist.
        // Also, KEEP IN MIND that the left and right child of the node correspond to "less than" and "greater than or equal to" threshold
        DTNode fillDTNode(ArrayList<Datum> datalist) {

            //ADD CODE HERE
            /***Data: data set (training)
             Result: the root node of a decision tree
             MAKE DECISION TREE NODE(data)
             if the labelled data set has at least k data items (see below) then
             if all the data items have the same label then
             create a leaf node with that class label and return it;
             else
             create a “best” attribute test question; (see details later)
             create a new node and store the attribute test in that node, namely attribute and threshold;
             split the set of data items into two subsets, data1 and data2, according to the answers to the test
             question;
             newNode.child1 = MAKE DECISION TREE NODE(data1)
             newNode.child2 = MAKE DECISION TREE NODE(data2)
             return newNode
             end
             else
             create a leaf node with label equal to the majority of labels and return it;
             end
             In the program, k is an argument of the decision tree construction minSizeDatalist.
             ***/
            DTNode root = new DTNode();

            if (datalist.size() >= minSizeDatalist) {
                int firstLabel = (datalist.get(0)).y;
                boolean isSameLabel = true;
                for (Datum item : datalist) {
                    if (item.y != firstLabel) {
                        isSameLabel = false;
                        break;
                    }
                }
                if (isSameLabel == true) {
                    root.label = firstLabel;
                    root.leaf = true;
                    root.left = null;
                    root.right = null;
                    return root;
                } else {
                    double Entropy = calcEntropy(datalist);
                    DTNode split = findBestSplit(datalist, Entropy);
                        if (split.leaf == true) {
                            root.leaf = true;
                            root.label = split.label;
                            root.left = null;
                            root.right = null;
                            return root;
                        } else {
                            root.attribute = split.attribute;
                            root.threshold = split.threshold;

                            ArrayList<Datum> data1 = new ArrayList<Datum>();
                            ArrayList<Datum> data2 = new ArrayList<Datum>();
                            for (Datum item : datalist) {
                                if (item.x[root.attribute] < root.threshold) {
                                    data1.add(item);
                                } else {
                                    data2.add(item);
                                }
                            }
                            if (data1 != null) {
                                //System.out.println(1);
                                root.left = fillDTNode(data1);
                            }
                            if (data2 != null) {
                                //System.out.println(2);
                                root.right = fillDTNode(data2);
                            }
                            root.leaf = false;
                            root.label = -1;
                            return root;
                        }
                    }

            } else {
                root.leaf = true;
                int majority = findMajority(datalist);
                root.label = majority;
                root.left = null;
                root.right = null;
                return root;
            }

        }

        /***Data: ADataset
         Result: Anattributeandathreshold FIND BEST SPLIT(data) {
         best avg entropy := inf;
         best attr := -1;
         best threshold := -1;
         for each attribute in x do
         for each data point in list do
         compute split and current avg entropy based on that split; if best avg entropy > current avg entropy then
         best avg entropy := current avg entropy; best attr := attribute;
         best threshold := value;
         end end
         end
         return (best attr, best threshold) }**/

        private DTNode findBestSplit(ArrayList<Datum> datalist, double Entropy) {
            double bestAvgEntropy = Double.POSITIVE_INFINITY;
            int bestAttribute = -1;
            double bestThreshold = -1;
            DTNode bestSplit = new DTNode();
            double currentEntropy = 1;

            if( Entropy > 0) {
                ArrayList<Double> D = new ArrayList<Double>();
                ArrayList<Datum> d1 = new ArrayList<Datum>();
                ArrayList<Datum> d2 = new ArrayList<Datum>();

                for (int index = 0; index < datalist.get(0).x.length; index++) {
                    for (Datum data : datalist) {
                        D.add(data.x[index]);
                    }
                    for (Double element : D) {
                        Double split = element;
                        for (int k = 0; k < D.size(); k++) {
                            if (D.get(k) < split) {
                                d1.add(datalist.get(k));
                            } else {
                                d2.add(datalist.get(k));
                            }
                        }
                        currentEntropy = ((((double) d1.size() / (double) datalist.size())) * calcEntropy(d1) + (((double) d2.size() / (double) datalist.size())) * calcEntropy(d2));
                        if (calcEntropy(datalist) > 0 && currentEntropy < calcEntropy(datalist)) {
                            if (bestAvgEntropy > currentEntropy) {
                                bestAvgEntropy = currentEntropy;
                                bestAttribute = index;
                                bestThreshold = split;
                                bestSplit.attribute = bestAttribute;
                                bestSplit.threshold = bestThreshold;
                            }
                        }
                        d1.clear();
                        d2.clear();
                    }
                    D.clear();
                }
            }
            if(Entropy == bestAvgEntropy){
                bestSplit.leaf = true;
                bestSplit.label = findMajority(datalist);
                //bestSplit.label = (int) bestAvgEntropy;
                //return bestSplit;
            }else{
                bestSplit.leaf = false;
                bestSplit.label = -1;
                //return bestSplit;
            }
            return bestSplit;
        }




        // This is a helper method. Given a datalist, this method returns the label that has the most
        // occurrences. In case of a tie it returns the label with the smallest value (numerically) involved in the tie.
        int findMajority(ArrayList<Datum> datalist) {

            int [] votes = new int[2];

            //loop through the data and count the occurrences of datapoints of each label
            for (Datum data : datalist)
            {
                votes[data.y]+=1;
            }

            if (votes[0] >= votes[1])
                return 0;
            else
                return 1;
        }




        // This method takes in a datapoint (excluding the label) in the form of an array of type double (Datum.x) and
        // returns its corresponding label, as determined by the decision tree
        int classifyAtNode(double[] xQuery) {

            //ADD CODE HERE
        /***Data: Adecisiontree,andanunlabelleddataitem(datum)tobeclassified Result: (Predicted) classification label
         CLASSIFY(node, datum) {
         if node is a leaf then
         return the label of that node i.e. classify;
         else
         test the data item using question stored at that (internal) node, and determine which child node to go to, based on the answer ;
         return CLASSIFY(child, datum);
         end
         }***/
        if(this.leaf == true){
            return this.label;
        }else{
            double answer = xQuery[this.attribute];
            if(answer<this.threshold){
                return this.left.classifyAtNode(xQuery);
            }
            else if(answer>=this.threshold){
                return this.right.classifyAtNode(xQuery);
            }
        }
            return -1; //dummy code.  Update while completing the assignment.
        }


        //given another DTNode object, this method checks if the tree rooted at the calling DTNode is equal to the tree rooted
        //at DTNode object passed as the parameter
        public boolean equals(Object dt2)
        {

            //ADD CODE HERE
            if (dt2 instanceof DTNode){

                if(((DTNode)dt2).leaf == true && this.leaf == true && this.label == ((DTNode) dt2).label) {
                    return true;
                }
                if(((DTNode)dt2).leaf == false && this.leaf == false){

                    return(this.attribute == ((DTNode) dt2).attribute && this.threshold == ((DTNode) dt2).threshold
                            && this.left.equals(((DTNode)dt2).left) && this.right.equals(((DTNode)dt2).right));
                }
            }
            return false; //dummy code.  Update while completing the assignment.
        }
    }



    //Given a dataset, this returns the entropy of the dataset
    double calcEntropy(ArrayList<Datum> datalist) {
        double entropy = 0;
        double px = 0;
        float [] counter= new float[2];
        if (datalist.size()==0)
            return 0;
        double num0 = 0.00000001,num1 = 0.000000001;

        //calculates the number of points belonging to each of the labels
        for (Datum d : datalist)
        {
            counter[d.y]+=1;
        }
        //calculates the entropy using the formula specified in the document
        for (int i = 0 ; i< counter.length ; i++)
        {
            if (counter[i]>0)
            {
                px = counter[i]/datalist.size();
                entropy -= (px*Math.log(px)/Math.log(2));
            }
        }

        return entropy;
    }


    // given a datapoint (without the label) calls the DTNode.classifyAtNode() on the rootnode of the calling DecisionTree object
    int classify(double[] xQuery ) {
        return this.rootDTNode.classifyAtNode( xQuery );
    }

    // Checks the performance of a DecisionTree on a dataset
    // This method is provided in case you would like to compare your
    // results with the reference values provided in the PDF in the Data
    // section of the PDF
    String checkPerformance( ArrayList<Datum> datalist) {
        DecimalFormat df = new DecimalFormat("0.000");
        float total = datalist.size();
        float count = 0;

        for (int s = 0 ; s < datalist.size() ; s++) {
            double[] x = datalist.get(s).x;
            int result = datalist.get(s).y;
            if (classify(x) != result) {
                count = count + 1;
            }
        }

        return df.format((count/total));
    }


    //Given two DecisionTree objects, this method checks if both the trees are equal by
    //calling onto the DTNode.equals() method
    public static boolean equals(DecisionTree dt1, DecisionTree dt2)
    {
        boolean flag = true;
        flag = dt1.rootDTNode.equals(dt2.rootDTNode);
        return flag;
    }

}

