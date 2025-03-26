"""
DO NOT MODIFY ANY GIVEN FIELDS OR COMPELTED FUNCTIONS
    You must implement getBestJoinOrder(self) and getCardinality(self, inRels : list)
    You can add new functions or variables if you wish
"""


class JoinGraph:
    """
    A class used to represent a join graph

    Fields
    -----------
    rels : [Relation]
        all join relations specified in the input file
    joinConditions : [JoinCondition]
        all join conditions specified in the input file
    """

    rels = None
    joinConditions = None

    def __init__(self, path: str) -> None:
        with open(path, "r") as f:
            lines = f.readlines()
            self._load(lines)

    def _load(self, lines: list) -> None:
        """
        Inject join relations and conditions
        """
        assert (len(lines) >= 3)
        numRels = int(lines[0])
        self.rels = [None] * numRels
        # inject join rels
        cardinalities = lines[1].split(",")
        assert (len(cardinalities) == numRels)
        relationNameIdxMap = {}
        for i in range(numRels):
            relationName = "R" + str(i)
            self.rels[i] = Relation(relationName, i, int(cardinalities[i].strip()))
            relationNameIdxMap[relationName] = i
        # inject foreign keys
        foreignRelationNames = lines[2].split(",")
        assert (len(foreignRelationNames) == numRels - 1)
        self.joinConditions = [None] * (numRels - 1)
        for i in range(numRels - 1):
            foreignRelationIdx = relationNameIdxMap[foreignRelationNames[i].strip()]
            if i == foreignRelationIdx:
                self.joinConditions[i] = JoinCondition(self.rels[i+1], self.rels[i])
            elif i + 1 == foreignRelationIdx:
                self.joinConditions[i] = JoinCondition(self.rels[i], self.rels[i+1])
            else:
                assert (False)

    def getBestJoinOrder(self):
        """
        Compute the join order with lowest cost.
        Return : JoinPlan
            root of the join tree
        """
        n = len(self.rels)
        if n == 0:
            return JoinPlan(None, None, [], 0)
        if n == 1:
            return JoinPlan(None, None, [self.rels[0]], self.rels[0].cardinality)

        dp = [[None for _ in range(n)] for _ in range(n)]

        for i in range(n):
            rel = self.rels[i]
            dp[i][i] = JoinPlan(None, None, [rel], rel.cardinality)

        for length in range(2, n + 1):
            for start in range(n - length + 1):
                end = start + length - 1
                best_plan = None
                for k in range(start, end):
                    left = dp[start][k]
                    right = dp[k + 1][end]
                    if left.estOutCard < right.estOutCard:
                        left, right = right, left
                    sub_rels = self.rels[start:end + 1]
                    cur_plan = JoinPlan(left, right, sub_rels, self.getCardinality(sub_rels))
                    if best_plan is None or cur_plan.estCost < best_plan.estCost:
                        best_plan = cur_plan
                dp[start][end] = best_plan

        return dp[0][n - 1]

    def getCardinality(self, inRels: list) -> int:
        """
        Compute cardinality given a list of join relations
        Input: [Relation]
        Output: int
            estimated output cardinality of given join relations
        """
        assert (len(inRels) >= 0)
        if len(inRels) == 0:
            return 0
        if len(inRels) == 1:
            return inRels[0].cardinality

        # Assume inRels are sorted by index.
        start_idx = inRels[0].idx
        end_idx = inRels[-1].idx

        # Calculate product of all relation cardinalities.
        numerator = 1
        for r in inRels:
            numerator *= r.cardinality

        # Calculate product of F(Rt, Rt+1) for t in [start_idx .. end_idx-1].
        denominator = 1
        for i in range(start_idx, end_idx):
            jc = self.joinConditions[i]  # join condition for R_i and R_{i+1}
            primary = jc.primaryRel
            denominator *= primary.cardinality

        est = numerator / denominator
        return int(est)


class Relation:
    """
    A class used to represent base relation table

    Fields
    -----------
    name : str
        name of the relation
    idx  : int
        index of the relation generated during injection
    cardinality : int
        cardinality of the relation
    """

    name = None
    idx = None
    cardinality = None

    def __init__(self, name: str, idx: int, cardinality: int) -> None:
        self.name = name
        self.idx = idx
        self.cardinality = cardinality

    def __str__(self) -> str:
        """
        Represent relation in the format of name(idx):cardinality
        E.g. A(0):50
        """
        return self.name + "(" + str(self.idx) + "):" + str(self.cardinality)

    def __repr__(self) -> str:
        return self.__str__()


class JoinCondition:
    """
    A class used to track foreign key for chain joins

    Fields
    -----------
    primaryRelation : Relation
        relation containing primary key
    foreignRelation : Relation
        relation containing foreign key
    """

    primaryRel = None
    foreignRel = None

    def __init__(self, primaryRel: Relation, foreignRel: Relation) -> None:
        self.primaryRel = primaryRel
        self.foreignRel = foreignRel


class JoinPlan:
    """
    A class used to represent a logical join tree

    Fields
    -----------
    left : JoinPlan
        left child
    right : JoinPlan
        right child
    rels : [Relation]
        join relations matched in the join tree
    estOutCard : int
        estimated output cardinality of the join tree
    estCost : int
        cost of the join tree
    """

    left = None
    right = None
    rels = None
    estOutCard = 0
    estCost = 0

    def __init__(self, left, right, rels: list, estOutCard: int) -> None:
        self.left = left
        self.right = right
        self.rels = rels
        self.estOutCard = int(estOutCard)
        assert (self.estOutCard != 0)
        if self.isLeaf():
            self.estCost = 0
        else:
            self.estCost = left.estCost + right.estCost + estOutCard

    def isLeaf(self) -> bool:
        """
        Check if this is the leaf
        """
        return self.left is None and self.right is None

    def __str__(self) -> str:
        """
        Represent relation in the format of JO(estOutCard)
        E.g. JO(50)
        """
        return "JO(" + str(self.estOutCard) + ")"

    def __repr__(self) -> str:
        return self.__str__()
