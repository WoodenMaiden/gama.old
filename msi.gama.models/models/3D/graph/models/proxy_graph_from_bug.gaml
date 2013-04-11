model Graph

import 'bug.gaml'


global{
	int distance parameter: 'Distance' min: 1 <- 50 category: 'Model';	
}

entities{
	
	
	species node mirrors: list(bug) parent: graph_node edge_species:edge {
		point location <- target.location update: target.location;
		
		bool related_to(node other){
			using topology(target){
				return (target distance_to other.target) < distance;
			}
		}
		
		aspect base{
		  draw sphere(1) color: rgb('green');
		}
		aspect dynamic{
		int degree <-(my_graph) degree_of(self);
		write "" + degree;
		  draw sphere(1+ (degree / 5.0)) color: rgb('blue');
		}

   }
   
   species edge parent:base_edge{	
   	rgb color;

    
   	aspect base{
   		draw shape color: rgb("green");
   	}
   	
   	aspect dynamic{
   	  float val <- 255.0 *  (shape.perimeter / distance);
   	  set  color <- color hsb_to_rgb ([val,1.0,1.0]);
   	  draw shape + 0.1 color:color border: color ;	
   	}
   }	
}

experiment basicGraph type: gui {
	output {

	    
	    display graph_plus_bug type:opengl ambiant_light:0.2 {
	    	species bug aspect:base;
	        species node aspect: base z:0 position: {125,0,0};
	        species edge aspect: base z:0 position: {125,0,0};
	        
	        species node aspect: dynamic z:0 position: {250,0,0};
	        species edge aspect: dynamic z:0 position: {250,0,0};
	    }
	} 
}
