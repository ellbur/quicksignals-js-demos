
import org.scalajs.dom
import org.scalajs.dom.{Node, Element}
import org.scalajs.dom.ext._
import org.scalajs.dom.document
import org.scalajs.dom.window.console
import quicksignals._

object Main {
  enum SeqSig[+A] {
    case SNil
    case SCons(head: A, tail: Target[SeqSig[A]])
    
    def map[B](f: A => B): SeqSig[B] = this match {
      case SNil => SNil
      case SCons(head, tail) => SCons(f(head), tail map (_ map f))
    }
  }
  import SeqSig._
    
  extension(node: Node) {
    def setChildren(children: Target[SeqSig[Node]])(using t: Tracking): Unit = {
      node.setChildren(0, children)
    }
    
    def setChildren(i: Int, children: Target[SeqSig[Node]])(using t: Tracking): Unit = {
      () setting { _ =>
        node.childNodes.toSeq.drop(i) foreach node.removeChild
        
        children.track match {
          case SNil =>
          case SCons(head, tail) => 
            node.appendChild(head)
            () setting { _ =>
              node.setChildren(i + 1, tail)
            }
        }
      }
    }
  }
  
  private def text(t: String): dom.Text = document.createTextNode(t)
  
  def main(args: Array[String]): Unit = {
    document.onreadystatechange = { _ =>
      if (document.readyState == "complete") {
        init()
      }
    }
  }
  
  def init(): Unit = {
    class Counter {
      val count = Source[Int](0)
    }
  
    val counters = Source[SeqSig[Counter]](SNil)
    val end = Source[Source[SeqSig[Counter]]](counters)
    
    {
      import scalatags.JsDom.all._
      document.body.appendChild(p(input(`type` := "text")).render)
    }
    
    val newButton = {
      import scalatags.JsDom.all._
      val b = button("New").render
      document.body.appendChild(p(b).render)
      b
    }
    
    val list = document.createElement("ul")
    document.body.appendChild(list)
    
    newButton.addEventListener("click", { _ =>
      val next = Source[SeqSig[Counter]](SNil)
      (end.now)() = SCons(Counter(), next)
      end() = next
    })
    
    val children = tracking {
      counters.track map { counter =>
        import scalatags.JsDom.all._
        
        val upButton = button("Up").render
        upButton.addEventListener("click", { _ =>
          counter.count() = counter.count.now + 1
        })
        
        val countField = span().render setting { s =>
          s.innerText = counter.count.track.toString
        }
        
        val testField = input(`type` := "text").render

        li(upButton, countField, testField).render
      }
    }

    val settingChildren = tracking {
      list setChildren children
    }
    
    settingChildren foreach { _ => }
  }
}

