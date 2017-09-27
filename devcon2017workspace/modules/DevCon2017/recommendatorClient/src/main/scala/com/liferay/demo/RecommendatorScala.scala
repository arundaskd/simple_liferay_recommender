package com.liferay.demo

import java.lang.{Long => JL, Double=>JD, Integer=>JI}
import java.util.{List => JList, Map => JMap}
import scala.collection.JavaConversions.seqAsJavaList
import scala.collection.JavaConverters._
import scala.util.Try

object RecommendatorScala {
  
  type D=Double; type S=String; type L=Long; type I=Int

  def jaccard(a:Map[S,I])(b:Map[S,I]):D={
    val i:D=(a.keySet intersect b.keySet).size
    i/(a.size+b.size-i)
  }

  def recommend(u:L, rat:Map[L,Map[S,I]])=
    (rat-u).foldLeft(Map[S,(D,D)]()){ case(ma,(_,r))=> 
        val d=jaccard(Try(rat(u)).getOrElse(Map[S,I]()))(r) 
        if(d>0)r.filterNot(rat(u).keySet contains _._1).foldLeft(ma){
          case(m,(k,v))=>m.updated(k,m.getOrElse(k,(0.0,0.0))match{case(x,y)=>(x+v*d,y+d)})
        }else ma
    }.map{case(k,(t,d))=>(t/d,k) }.toList.sorted.reverse 
}