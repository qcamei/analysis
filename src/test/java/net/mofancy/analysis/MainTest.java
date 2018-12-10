package net.mofancy.analysis;

import net.mofancy.analysis.util.CryptUtils;

public class MainTest {

	public static void main(String[] args) {
		String query = "SELECT t.*,CASE WHEN overall_spend!=0 THEN round(total_spend*100.0/overall_spend,4) END AS perc_spend" +
				"\n FROM (SELECT p.prod_desc,p.prod_code,p.prod_sys_key,pm.total_cust,pm.total_spend,g.group_name AS prev_group" +
				"\n   ,CASE WHEN pm.total_quantity=0 THEN 0 ELSE round(pm.total_spend/pm.total_quantity,2) END AS avg_price" +
				"\n   ,sum(pm.total_spend) OVER () AS overall_spend" +
				"\n FROM  prod_cluster_sku  c" +
				"\n INNER JOIN product_sku p ON p.prod_type=  'typeSKU'   AND c.sku=p.prod_sys_key" +
				"\n LEFT OUTER JOIN prod_clustered_group g ON g.proj=c.proj AND c.prev_cluster=g.group_key AND g.solution_key=" + 1 +
				"\n LEFT OUTER JOIN  product_summary  pm ON pm.proj=c.proj AND c.sku=pm.prod_sys_key" +
				"\n WHERE c.proj=" + 1 + " AND c.cluster_key=" + 1 +
				"\n ) t ORDER BY total_spend DESC NULLS LAST";
		System.out.println(query);
	}

}
