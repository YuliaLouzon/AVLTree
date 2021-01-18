import java.util.ArrayList;
import java.util.List;

/**
 *
 * 
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 *
 */

public class AVLTree {

	protected IAVLNode root;
	protected int size;
	protected IAVLNode min;
	protected IAVLNode max;

	public AVLTree() {
		this.root = new AVLNode();// creating root as external node
		this.size=0;
		this.min=null;
		this.max=null;
	}

	/**
	 * public boolean empty()
	 * <p>
	 * returns true if and only if the tree is empty
	 * complexity O(1)
	 */
	public boolean empty() {
		return !root.isRealNode();
	}

	/**
	 * public String search(int k)
	 * <p>
	 * returns the info of an item with key k if it exists in the tree
	 * otherwise, returns null
	 * complexity O(log n)
	 */
	public String search(int k) {
		IAVLNode curr = this.root;
		while (curr.isRealNode()){
			if (curr.getKey()== k) return curr.getValue();
			if (k>curr.getKey()){
				curr= curr.getRight();
			}
			else{
				curr= curr.getLeft();
			}
		}
		return null;
	}

	/**
	 * public int insert(int k, String i)
	 * <p>
	 * inserts an item with key k and info i to the AVL tree.
	 * the tree must remain valid (keep its invariants).
	 * returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
	 * promotion/rotation - counted as one re-balance operation, double-rotation is counted as 2.
	 * returns -1 if an item with key k already exists in the tree.
	 * complexity O(log n)
	 */
	public int insert(int k, String i) {
		IAVLNode curr = this.getRoot();
		IAVLNode parent = null;
		int counter=0;

		// insert a root if the tree is empty and update the fields
		if (empty()){
			this.root = new AVLNode(k, i);
			this.root.setSize(1);
			this.size= 1;
			this.max= this.root;
			this.min= this.root;
			return counter;
		}

		// checking if an item with key k already exists
		if (search(k)!=null) return -1;

		// searching for the parent of the new node -complexity O(log n)
		while (curr.isRealNode()){
			parent = curr;
			if (curr.getKey() > k) curr = curr.getLeft();
			else curr = curr.getRight();
		}
		IAVLNode newNode = new AVLNode(k, i);

		// updating the min max and size of the tree if needed - O(1)
		if (this.min.getKey()>k )this.min= newNode;
		if (this.max.getKey()<k ) this.max= newNode;
		this.size= this.size+1;

		//insert the new node as the child  - O(1)
		if (parent.getKey() > k) parent.setLeft(newNode);
		else parent.setRight(newNode);
		newNode.setParent(parent);

		curr= newNode;
		// updating the size of all the nodes which are affected by the insertion - O(log n)
		updateBranchSize(parent,'P');

		//Case B- if the parent is a leaf :
		if(parent.getHeight()==0) {

			while (curr.getParent() != null) {
				curr = curr.getParent();
				while (!isBalanced(curr)) {
					//Case 1 - 1,0 0,1 - promote
					if (getRankDifference(curr) == 1) {
						curr.setHeight(curr.getHeight() + 1); //**
						counter++;
					}
					else {
						AVLTree.IAVLNode child;
						//cases 2+3 similar to those in the slides
						if (curr.getHeight() - curr.getLeft().getHeight() == 0) {
							child = curr.getLeft(); // left child is needed to rotate

							//Case 2
							if (child.getHeight() - child.getLeft().getHeight() == 1) {
								rotate(child, 'R');
								// updating the sizes of the changed node
								curr.setSize(curr.getLeft().getSize()+ curr.getRight().getSize()+1);
								child.setSize(child.getLeft().getSize()+ child.getRight().getSize()+1);
								curr.setHeight(curr.getHeight() - 1);
								counter= counter +2;
								break;
							}
							// Case 3
							else {
								AVLTree.IAVLNode grandchild = child.getRight();
								caseC(grandchild,curr,child,'L','R');
								counter= counter+5;
								break;
							}
						}
						// symmetry
						else {
							//cases 2+3 symmetric to those in the slides
							child = curr.getRight();
							//Case 2
							if(child.getHeight() - child.getRight().getHeight() == 1) {
								rotate(child, 'L');
								curr.setSize(curr.getLeft().getSize()+ curr.getRight().getSize()+1);
								child.setSize(child.getLeft().getSize()+ child.getRight().getSize()+1);
								curr.setHeight(curr.getHeight() - 1);
								counter= counter +2;
								break;
							}
							// Case 3
							else {
								AVLTree.IAVLNode grandchild = child.getLeft();
								caseC(grandchild,curr,child,'R','L');
								counter= counter+5;
								break;
							}
						}
					}
				}
			}
		}
		return counter;
	}

	/**
	 * public int delete(int k)
	 * <p>
	 * deletes an item with key k from the binary tree, if it is there;
	 * the tree must remain valid (keep its invariants).
	 * returns the number of re-balancing operations, or 0 if no re-balancing operations were needed.
	 * demotion/rotation - counted as one re-balance operation, double-rotation is counted as 2.
	 * returns -1 if an item with key k was not found in the tree.
	 * complexity O(log n)
	 *
	 */
	public int delete(int k) {
		int counter=0;
		IAVLNode node = null;
		IAVLNode curr = this.root;
		//finding the node in the tree
		while (curr.isRealNode()){
			if (curr.getKey()== k){
				node = curr;
				break;
			}
			if (k>curr.getKey())curr= curr.getRight();
			else curr= curr.getLeft();
		}
		// checking if the given key found
		if(node == null) return -1;

		// checking if the given key is the root
		IAVLNode z= node.getParent();
		IAVLNode ex = new AVLNode();
		// checking if the tree include the root only
		if (this.root.getKey()==k && this.size==1){
			this.root=ex;
			this.size=0;
			updateMinMax();
			return counter;
		}
		// checking if the tree include the root and one child
		if (this.root.getKey()==k && this.size==2){
			if(root.getRight().isRealNode()) {
				this.root = node.getRight();
				node.getRight().setParent(null);
			}
			else{
				this.root = node.getLeft();
				node.getLeft().setParent(null);
			}
			this.size=1;
			updateMinMax();
			return counter;
		}
		// binary node - regular deletion
		if (node.getLeft().isRealNode() && node.getRight().isRealNode()){
			IAVLNode suc = successor(node);
			delete(suc.getKey());
			this.size ++;
			suc.setParent(z);
			suc.setLeft(node.getLeft());
			suc.setRight(node.getRight());
			updateNodeSize(suc);
			suc.setHeight(node.getHeight());
			if (node.getKey()== root.getKey()){
				this.root = suc;
			}
			if (z!=null && z.getKey()>node.getKey()) z.setLeft(suc);
			else if(z!=null && z.getKey()<node.getKey() )z.setRight(suc);
			node.getRight().setParent(suc);
			node.getLeft().setParent(suc);
			if (z!=null) z= z.getParent();
		}
		//the node is a leaf - Case 1
		else if (node.getHeight() == 0){
			updateBranchSize(z,'M');
			// case 1.1 - rank difference 1-1
			if (getRankDifference(z)==0) {
				connectParentToChild(node,z,ex);
				this.size--;
				updateMinMax();
				return counter;
			}
			// case 1.2 - rank difference 1-2 - demote z
			else if (z.getHeight()- node.getHeight()==1){
				connectParentToChild(node,z,ex);
				z.setHeight(z.getHeight()-1);
				//roll the problem to the top
				z= z.getParent();
				counter++;
			}

			// case 1.3 - rank difference 2-1 going to balancing
			else{
				connectParentToChild(node,z,ex);
			}
		}

		//the node is an unary node - Case 2
		else if (!node.getLeft().isRealNode() || !node.getRight().isRealNode()){
			// the base - 2.1 - connect the node son to his parent
			updateBranchSize(z,'M');
			IAVLNode x;
			if (node.getLeft().isRealNode()) x=node.getLeft();
			else x=node.getRight();

			if (getRankDifference(z)==0) {
				connectParentToChild(node,z,x);
				this.size--;
				updateMinMax();
				return counter;
			}
			// case 2.2- rank difference 1-2
			if(z.getHeight()-node.getHeight()==1){
				connectParentToChild(node,z,x);
				z.setHeight(z.getHeight()-1);
				counter++;
				z= z.getParent();
			}
			// case 2.3 - rank difference 3-1 going to balancing
			else{
				connectParentToChild(node,z,x);
			}
		}

		// re-balancing the tree
		while (z!=null && !isBalanced(z)){
			// case 1.2b 2.2b - 2 2
			if(getRankDifference(z)==0){

				z.setHeight(z.getHeight()-1);
				counter++;
				z= z.getParent();
			}
			// all the cases for case 2.3 - 1.3
			else{
				if(z.getHeight()-z.getRight().getHeight()==1) {
					IAVLNode u= z.getRight();
					//case 2.3.1
					if (getRankDifference(u)==0){
						this.rotate(u,'L');
						counter = counter+3;
						z.setHeight(z.getHeight()-1);
						u.setHeight(u.getHeight()+1);
						updateNodeSize(z);
						updateNodeSize(u);
						this.size--;
						updateMinMax();
						return counter;
					}
					//case 2.3.2
					else if(u.getHeight()-u.getLeft().getHeight()==2){
						this.rotate(u,'L');
						counter = counter+3;
						z.setHeight(z.getHeight()-2);
						updateNodeSize(z);
						updateNodeSize(u);
						z= u.getParent();
					}
					//case 2.3.3
					else{
						IAVLNode a;
						if(u.getHeight()-u.getLeft().getHeight()==1) a=u.getLeft();
						else a=u.getRight();
						caseCDelete(a,z,u,'R','L');
						counter= counter+6;
						z = a.getParent();
					}
				}
				else {
					//case 2.3.1 sim
					IAVLNode u = z. getLeft();
					if (getRankDifference(u)==0){
						rotate(u,'R');
						counter = counter+3;
						z.setHeight(z.getHeight()-1);
						u.setHeight(u.getHeight()+1);
						updateNodeSize(z);
						updateNodeSize(u);
						this.size--;
						updateMinMax();
						return counter;
					}
					//Case 2.3.2 symmetric
					if(u.getHeight() - u.getRight().getHeight() == 2) {
						this.rotate(u, 'R');
						z.setHeight(z.getHeight() - 2);
						counter = counter + 3;
						updateNodeSize(z);
						updateNodeSize(u);
						z = u.getParent();
					}
					//Case 2.3.3 symmetric
					else {
						IAVLNode a;

						if (u.getHeight() - u.getRight().getHeight() == 1) a = u.getRight();
						else a = u.getLeft();
						caseCDelete(a,z,u,'L','R');
						counter = counter + 6;
						z = a.getParent();
					}
				}

			}
		}
		updateMinMax();
		this.size--;
		return counter;    // to be replaced by student code
	}


	/**
	 * public String min()
	 * <p>
	 * Returns the info of the item with the smallest key in the tree,
	 * or null if the tree is empty
	 *  we maintain the field min in the insertion and deletion so - complexity O(1)
	 */
	public String min() {
		if (empty()) return null;
		return this.min.getValue();
	}

	/**
	 * public String max()
	 * <p>
	 * Returns the info of the item with the largest key in the tree,
	 * or null if the tree is empty
	 * we maintain the field max in the insertion and deletion so - complexity O(1)
	 */
	public String max() {
		if (empty()) return null;
		return this.max.getValue();
	}

	/**
	 * public int[] keysToArray()
	 * <p>
	 * Returns a sorted array which contains all keys in the tree,
	 * or an empty array if the tree is empty.
	 * complexity O(n)
	 */
	public int[] keysToArray() {
		int[] result= new int[this.size];
		if (empty()) return result;
		keysToArrayRec(this.root,result,0);
		return result;
	}
	/**
	 * public String[] infoToArray()
	 * <p>
	 * Returns an array which contains all info in the tree,
	 * sorted by their respective keys,
	 * or an empty array if the tree is empty.
	 * complexity O(n)
	 */
	public String[] infoToArray() {
		String[] result = new String[this.size];
		if (empty()) return result;
		infoToArrayRec(this.root,result,0);
		return result;
	}

	/**
	 * public int size()
	 * <p>
	 * Returns the number of nodes in the tree.
	 * <p>
	 * we maintain the size each time we insert or delete so complexity O(1)
	 * precondition: none
	 * post-condition: none
	 */
	public int size() {
		return this.size;
	}

	/**
	 * public int getRoot()
	 * <p>
	 * Returns the root AVL node, or null if the tree is empty
	 * <p>
	 * O(1)
	 * precondition: none
	 * post-condition: none
	 */
	public IAVLNode getRoot() {
		return this.root;
	}

	/**
	 * public string split(int x)
	 * <p>
	 * splits the tree into 2 trees according to the key x.
	 * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
	 * precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
	 * post-condition: none
	 * complexity O(log n)
	 */
	public AVLTree[] split(int x) {

		// finding the node
		IAVLNode curr= findNode(x);

		// updating the trees
		AVLTree t1= createTree(curr.getLeft());
		AVLTree	t2= createTree(curr.getRight());
		AVLTree[] result=new AVLTree[2];
		result[0]= t1;
		result[1]=t2;

		IAVLNode parent = curr.getParent();
		if (parent==null) {
			if(!result[0].empty())t1.updateMinMax();
			if(!result[1].empty()) t2.updateMinMax();
			return result;
		}
		// loop until the root
		while (parent !=null) {
			// the curr node is a right child
			if (parent.getKey() < curr.getKey()) {
				AVLTree currTree= createTree(parent.getLeft());
				IAVLNode fake = parent.getParent();
				t1.join(parent, currTree);
				parent= fake;
			}
			// the curr node is a left child
			else{
				AVLTree currTree= createTree(parent.getRight());
				IAVLNode fake = parent.getParent();
				t2.join(parent, currTree);
				parent= fake;
			}
		}
		if(!result[0].empty())result[0].updateMinMax();
		if(!result[1].empty()) result[1].updateMinMax();
		return result;
	}


	/**
	 * public join(IAVLNode x, AVLTree t)
	 * <p>
	 * joins t and x with the tree.
	 * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
	 * precondition: keys(x,t) < keys() or keys(x,t) > keys(). t/tree might be empty (rank = -1).
	 * complexity O(log n)
	 * post-condition: none
	 */
	public int join(IAVLNode x, AVLTree t) {
		x.setParent(null);
		AVLTree t1;
		AVLTree t2;
		int complexity = Math.abs(this.getRoot().getHeight() - t.getRoot().getHeight()) + 1;
		IAVLNode updatedRoot;
		//check which tree is smaller
		if (this.getRoot().getHeight() < t.getRoot().getHeight()) {
			t1 = this;
			t2 = t;
			updatedRoot = t.getRoot();
		}
		else {
			t1 = t;
			t2 = this;
			updatedRoot = this.root;
		}

		//if this is an empty tree;
		if (this.size() == 0) {
			t.insert(x.getKey(), x.getValue());
			this.root = t.getRoot();
			this.size = t.size();
			t.updateMinMax();
			return Math.abs(-1 - t.getRoot().getHeight()) + 1;
		}
		//if t is an empty tree
		else if (t.size() == 0) {
			this.insert(x.getKey(), x.getValue());
			return Math.abs(this.getRoot().getHeight() + 1) + 1;
		}

		if (t1.getRoot().getHeight() == t2.getRoot().getHeight()) {
			if (x.getKey() < t2.getRoot().getKey()){
				x.setRight(t2.getRoot());
				x.setLeft(t1.getRoot());
			}
			else {
				x.setRight(t1.getRoot());
				x.setLeft(t2.getRoot());
			}
			//update nodes
			t2.getRoot().setParent(x);
			t1.getRoot().setParent(x);
			x.setHeight(x.getLeft().getHeight() + 1);
			x.setSize(x.getLeft().getSize() + x.getRight().getSize() + 1);
			this.root = x;
			this.size = this.getRoot().getSize();
			updateMinMax();
			return 1;
		}

		IAVLNode b = t2.getRoot();
		IAVLNode a = t1.getRoot();
		IAVLNode c;

		//check if t2 is the right sub tree
		if (x.getKey() < t2.getRoot().getKey()) {
			//find the parent of the node with rank <= k in t2
			while (b.getHeight() > t1.getRoot().getHeight()) {
				b = b.getLeft();
			}
			c = b.getParent();

			//update a
			a.setParent(x);

			//update b
			b.setParent(x);

			//update x
			x.setParent(c);
			x.setLeft(a);
			x.setRight(b);
			x.setHeight(Math.max(b.getHeight(), a.getHeight()) + 1);
			x.setSize(a.getSize() + b.getSize() + 1);

			//update c - rank is not updated cause it might need balancing
			c.setLeft(x);
			c.setSize(c.getRight().getSize() + x.getSize() + 1);

			//update the new tree
			updateSizeJoin(c);
			this.root = updatedRoot;
			this.size = this.getRoot().getSize(); //??? maybe update the size for the whole branch starting from c and up?
			updateMinMax();
		}
		//t2 is the left sub tree
		else {
			//find the parent of the node with rank <= k in t2
			while (b.getHeight() > t1.getRoot().getHeight()) {
				b = b.getRight();
			}
			c = b.getParent();

			//update a
			a.setParent(x);

			//update b
			b.setParent(x);

			//update x
			x.setParent(c);
			x.setLeft(b);
			x.setRight(a);
			x.setHeight(Math.max(b.getHeight(), a.getHeight()) + 1);
			x.setSize(a.getSize() + b.getSize() + 1);

			//update c - rank is not updated cause it might need balancing
			c.setRight(x);
			c.setSize(c.getLeft().getSize() + x.getSize() + 1);

			//update the new tree
			updateSizeJoin(c);
			this.root = updatedRoot;
			this.size = this.getRoot().getSize();
			updateMinMax();
		}

		//check if the join creates a zero rank difference
		int diff = c.getHeight() - x.getHeight();
		IAVLNode cParent;
		while (diff == 0){
			cParent = c.getParent();
			this.balancing(c);
			if (cParent != null) {
				diff = Math.min(cParent.getHeight() - cParent.getLeft().getHeight(), cParent.getHeight() - cParent.getRight().getHeight());
				c = cParent;
			}
			else {
				break;
			}
		}

		return complexity;
	}


	/** update the size field of the node from the sizes of his children- complexity O(log n) **/
	private void updateSizeJoin(IAVLNode c) {
		IAVLNode parent;
		while (c.getParent() != null) {
			parent = c.getParent();
			parent.setSize(parent.getRight().getSize() + parent.getLeft().getSize() + 1);
			c = parent;
		}
	}
	/** used in the join method- balancing operations after joining the trees complexity O(1) **/
	private void balancing(IAVLNode node) {
		//rank diff c-x is 0 and rank diff c-other child is 1
		if (getRankDifference(node) == 1) {
			node.setHeight(node.getHeight() + 1);
		}
		//rank c is 0 - 2
		else{
			AVLTree.IAVLNode child;
			//cases 2+3 similar to those in the slides
			if (node.getHeight() - node.getLeft().getHeight() == 0) {
				child = node.getLeft(); // left child is needed to rotate
				//Case only for join
				if (this.getRankDifference(child) == 0){
					rotate(child, 'R');
					node.setSize(node.getLeft().getSize()+ node.getRight().getSize()+1);
					child.setSize(child.getLeft().getSize()+ child.getRight().getSize()+1);
					child.setHeight(child.getHeight() + 1);
				}

				//Case 2
				else if (child.getHeight() - child.getLeft().getHeight() == 1) {
					rotate(child, 'R');
					// updating the sizes of the changed node
					node.setSize(node.getLeft().getSize()+ node.getRight().getSize()+1);
					child.setSize(child.getLeft().getSize()+ child.getRight().getSize()+1);
					node.setHeight(node.getHeight() - 1);
				}
				// Case 3
				else {
					AVLTree.IAVLNode grandchild = child.getRight();
					caseC(grandchild,node,child,'L','R');
				}
			}
			// symmetry
			else {
				//cases 2+3 symmetric to those in the slides
				child = node.getRight();
				//Case only for join
				if (this.getRankDifference(child) == 0){
					rotate(child, 'L');
					node.setSize(node.getLeft().getSize()+ node.getRight().getSize()+1);
					child.setSize(child.getLeft().getSize()+ child.getRight().getSize()+1);
					child.setHeight(child.getHeight() + 1);
				}
				//Case 2
				else if(child.getHeight() - child.getRight().getHeight() == 1) {
					rotate(child, 'L');
					node.setSize(node.getLeft().getSize()+ node.getRight().getSize()+1);
					child.setSize(child.getLeft().getSize()+ child.getRight().getSize()+1);
					node.setHeight(node.getHeight() - 1);
				}
				// Case 3
				else {
					AVLTree.IAVLNode grandchild = child.getLeft();
					caseC(grandchild,node,child,'R','L');
				}
			}
		}
	}

	/** updating the node size O(1) **/
	private void updateNodeSize(IAVLNode node){
		node.setSize(node.getLeft().getSize() + node.getRight().getSize() + 1);
	}
	/** return the successor of a given node complexity - O(log n) **/
	private IAVLNode successor(IAVLNode node){
		IAVLNode suc = node.getRight();
		while(suc.getLeft().getHeight()>=0){
			suc=suc.getLeft();
		}
		return suc;
	}
	/** rotating the tree by the given node and direction complexity- O(1) **/
	public void rotate(IAVLNode x,char direction){
		IAVLNode parent= x.getParent();
		IAVLNode gran=null;
		if (parent!=null) {
			gran = parent.getParent();
		}
		IAVLNode child;
		if (direction=='R') child=x.getRight();
		else child=x.getLeft();

		// checking if its around the root
		if (parent!=null && parent.getKey()== this.root.getKey()) {
			this.root = x;
		}
		//update parent
		if (parent!= null && gran!= null) {
			x.setParent(gran);
			if (gran.getKey() > x.getKey()) {
				gran.setLeft(x);
			} else {
				gran.setRight(x);
			}
		}
		// update x
		x.setParent(gran);
		if (direction=='R') x.setRight(parent);
		else x.setLeft(parent);
		// update parent
		if (parent!=null){
			parent.setParent(x);
			if (direction=='R') parent.setLeft(child);
			else parent.setRight(child);
		}
		// update rightChild
		child.setParent(parent);
	}
	/** checking if the subtree of the given node is an AVL balanced tree  complexity O(1)**/
	protected boolean isBalanced(IAVLNode node) {
		if (node == null) return true;
		if (node.getRight().getKey() == -1 && node.getLeft().getKey() == -1) {
			return true;
		}
		int leftDif = node.getHeight() - node.getLeft().getHeight();
		int rightDif = node.getHeight() - node.getRight().getHeight();
		return (leftDif == 2 && rightDif == 1) || (rightDif == 2 && leftDif == 1) || (leftDif == 1 && rightDif == 1);
	}
	/** calculation the difference of the rank-difference between the child of the nodes complexity O(1)**/
	protected int getRankDifference(IAVLNode node){
		int leftDif = node.getHeight() - node.getLeft().getHeight();
		int rightDif = node.getHeight() - node.getRight().getHeight();
		return Math.abs(leftDif-rightDif);
	}
	/** updating the min and max of the tree with complexity O(log n)**/
	protected void updateMinMax() {
		//if the deleted node is the only node in the tree
		if (this.size() == 0) {
			IAVLNode n = new AVLNode();
			this.min = n;
			this.max = n;
			return;
		}
		//if there is more than 1 node in the tree

		//update the min
		IAVLNode node = this.getRoot();
		while (node.getLeft().isRealNode()) {
			node = node.getLeft();
		}
		this.min = node;

		//update the max
		node = this.getRoot();
		while (node.getRight().isRealNode()) {
			node = node.getRight();
		}
		this.max = node;
	}
	/** searching the node by the given key and returning its avlNode -complexity O(log n) **/
	protected IAVLNode findNode(int x){
		IAVLNode curr= this.root;
		while (curr.isRealNode()){
			if (curr.getKey()== x){
				return curr;
			}
			if (x>curr.getKey())curr= curr.getRight();
			else curr= curr.getLeft();
		}
		return null;
	}


	/** creating a tree by the given root complexity O(1) **/
	protected AVLTree createTree(IAVLNode child) {
		AVLTree t1= new AVLTree();
		t1.root= child;
		t1.root.setParent(null);
		t1.size= child.getSize();
		t1.min = child;
		t1.max = child;
		return t1;
	}
	/** recursive method that help building a sorted value -array inorder traversal - O(n) **/
	private int infoToArrayRec(IAVLNode node, String[] result,int counter) {
		if (node.getLeft().isRealNode()) {
			counter = infoToArrayRec(node.getLeft(), result, counter);
		}
		result[counter] = node.getValue();
		counter++;
		if(node.getRight().isRealNode()){
			counter= infoToArrayRec(node.getRight(),result,counter);
		}
		return counter;
	}
	/** recursive method that help building a sorted key -array inorder traversal - O(n) **/
	private int keysToArrayRec(IAVLNode node, int[] result,int counter) {
		if (node.getLeft().isRealNode()){
			counter= keysToArrayRec(node.getLeft(),result,counter);
		}
		result[counter] = node.getKey();
		counter++;
		if(node.getRight().isRealNode()){
			counter= keysToArrayRec(node.getRight(),result,counter);
		}
		return counter;
	}
	/** double rotation delete for cases 3(symmetry) - complexity O(1) **/
	protected void caseCDelete(IAVLNode a, IAVLNode z, IAVLNode u, char first, char second) {
		rotate(a,first);
		rotate(a,second);
		z.setHeight(z.getHeight()-2);
		u.setHeight(u.getHeight()-1);
		a.setHeight(u.getHeight()+1);
		updateNodeSize(z);
		updateNodeSize(u);
		updateNodeSize(a);
	}
	/** double rotation for cases 3(symmetry) - complexity O(1) **/
	protected void caseC(IAVLNode grandchild,IAVLNode curr,IAVLNode child,char first,char second){
		rotate(grandchild,first);
		rotate(grandchild,second);
		curr.setHeight(curr.getHeight()-1);
		child.setHeight(child.getHeight()-1);
		grandchild.setHeight(grandchild.getHeight()+1);
		child.setSize(child.getLeft().getSize()+ child.getRight().getSize()+1);
		curr.setSize(curr.getLeft().getSize()+ curr.getRight().getSize()+1);
		grandchild.setSize(grandchild.getLeft().getSize()+ grandchild.getRight().getSize()+1);
	}

	/** updating the size of the branch according to the operation plus or minus- complexity Oׂׂ(log n) **/
	protected void updateBranchSize(IAVLNode parent, char operation){
		if (parent==null)return;
		if ( operation=='P') {
			parent.setSize(parent.getSize() + 1);
			while (parent.getParent() != null) {
				parent = parent.getParent();
				parent.setSize(parent.getSize() + 1);
			}
		}
		else{
			parent.setSize(parent.getSize() - 1);
			while (parent.getParent() != null) {
				parent = parent.getParent();
				parent.setSize(parent.getSize() - 1);
			}
		}
	}

	/** setting the given nodes ex and z to be child and parent -complexity O(1) **/
	private void connectParentToChild(IAVLNode node,IAVLNode z,IAVLNode ex){
		if (z!=null) {
			if (node.getKey() > z.getKey()) z.setRight(ex);
			else z.setLeft(ex);
			z.setSize(z.getSize() - 1);
			ex.setParent(z);
		}
		return;
	}

	/**
	 * public interface IAVLNode
	 * ! Do not delete or modify this - otherwise all tests will fail !
	 */
	public interface IAVLNode {
		int getKey(); //returns node's key (for virtual-val node return -1)

		String getValue(); //returns node's value [info] (for virtual-val node return null)

		void setLeft(IAVLNode node); //sets left child

		IAVLNode getLeft(); //returns left child (if there is no left child return null)

		void setRight(IAVLNode node); //sets right child

		IAVLNode getRight(); //returns right child (if there is no right child return null)

		void setParent(IAVLNode node); //sets parent

		IAVLNode getParent(); //returns the parent (if there is no parent return null)

		boolean isRealNode(); // Returns True if this is a non-virtual AVL node

		void setHeight(int height); // sets the height of the node

		int getHeight(); // Returns the height of the node (-1 for virtual nodes)

		int getSize(); // Returns the size of the node

		void setSize(int size); // sets the size of the node

	}

	/**
	 * public class AVLNode
	 * <p>
	 * If you wish to implement classes other than AVLTree
	 * (for example AVLNode), do it in this file, not in
	 * another file.
	 * This class can and must be modified.
	 * (It must implement IAVLNode)
	 */
	public class AVLNode implements IAVLNode {
		private int key;
		private String value;
		private IAVLNode parent;
		private IAVLNode left;
		private IAVLNode right;
		private int rank;
		private int size;
		public AVLNode(int key,String value) {
			this.key = key;
			this.value = value;
			AVLNode l = new AVLNode();
			AVLNode r = new AVLNode();
			r.setParent(this);
			l.setParent(this);
			this.parent = null;
			this.left = l;
			this.right = r;
			this.size=1;
			this.rank = 0;
		}

		/* constructor of external node */
		public AVLNode() {
			this.key = -1;
			this.value = null;
			this.parent = null;
			this.left = null;
			this.right = null;
			this.size=0;
			this.rank = -1;
		}

		/* All of the following methods are either setting a field in an AVLNode or reading a value from it,
		thus making all of those methods O(1)

		 */
		public int getKey() {
			return this.key;
		}

		public String getValue() {
			return this.value;
		}
		public void setLeft (IAVLNode node) {
			this.left = node;
		}
		public IAVLNode getLeft () {
			return this.left;
		}
		public void setRight(IAVLNode node) {
			this.right = node;
		}
		public IAVLNode getRight () {
			return this.right;
		}
		public void setParent(IAVLNode node){
			this.parent = node;
		}
		public IAVLNode getParent(){
			return this.parent;
		}
		// Returns True if this is a non-virtual AVL node
		public boolean isRealNode(){
			return this.rank != -1;
		}

		/*
		Both height and size are being updated during insertion and deletion, thus retrieving them cost O(1)
		 */
		public void setHeight(int height)
		{
			this.rank = height;
		}
		//O(1)
		public int getHeight(){
			return this.rank;
		}
		public int getSize(){
			return this.size;
		}
		public void setSize(int i){
			this.size=i;
		}
	}

}



