package com.liferay.demo

import java.lang.{Long => JL, Double=>JD, Integer=>JI}
import java.util.{List => JList, Map => JMap}
import scala.collection.JavaConversions.seqAsJavaList
import scala.collection.JavaConverters._
import scala.util.Try

object RecommendatorScala2 {
  
  type D=Double; type S=String; type L=Long; type I=Int

  /*** Simple intersection distance between 2 people(a & b) */
  def simple(a:Map[S,I])(b:Map[S,I]):D=(a.keySet intersect b.keySet).size
  
  /*** Jaccard distance between 2 people(a & b) */
  def jaccard(a:Map[S,I])(b:Map[S,I]):D={
    val i:D=(a.keySet intersect b.keySet).size
    i/(a.size+b.size-i)
  }


  /*** Euclidean distance(Sum of squared diff) between a & b */
  def euclidean(a:Map[S,I])(b:Map[S,I]):D={
    val i=a.keySet intersect b.keySet
    if(i.size==0) 0 
    else 1/(1.0+i.map(k=>Math.pow(a(k)-b(k),2)).sum) 
  }

  /*** The recommendation function itself 
   * 
   * for each of the other users get the distance with my user
   * 	for each item that other user rated (that my user hasn't), accumulate:
   * 	---rating x distance
   * 	---distance
   * 
   * the estimated rating for each item = 
   * (accumulated rating x distance)/(acumulated distance)
   * 
   * */
  def recommend(u:L, rat:Map[L,Map[S,I]], fDist:(Map[S,I])=>(Map[S, I])=>D)=
    (rat-u).foldLeft(Map[S,(D,D)]()){ case(ma,(_,r))=> 
        val d=fDist(Try(rat(u)).getOrElse(Map[S,I]()))(r) 
        if(d>0)r.filterNot(rat(u).keySet contains _._1).foldLeft(ma){
          case(m,(k,v))=>m.updated(k,m.getOrElse(k,(0.0,0.0))match{case(x,y)=>(x+v*d,y+d)})
        }else ma
    }.map{case(k,(t,d))=>(t/d,k) }.toList.sorted.reverse 
}