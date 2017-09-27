package com.liferay.demo

import java.lang.{Long => JL, Double=>JD, Integer=>JI}
import java.util.{List => JList, Map => JMap}
import scala.collection.JavaConversions.seqAsJavaList
import scala.collection.JavaConverters._
import scala.util.Try

object SRecommendator{
  
  type D=Double; type S=String; type L=Long; type I=Int

  /*** An adaptor so that it can be used from java; converts scala Maps into Java Maps, and so on*/
  def recommend(u:JL,r:JMap[JL,JMap[S,JI]]): JMap[S, JD] ={
    RecommendatorScala2.recommend(u.longValue,r.asScala.toMap.map{
      case(k,v)=>(k.longValue(),v.asScala.toMap.map{case(a,b)=>(a,b.intValue)})
    },RecommendatorScala2.jaccard).toMap.map{case(k,v)=>(v,new JD(k))}.asJava
  }

}