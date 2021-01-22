
import org.scalajs.dom
import org.scalajs.dom.{Node, Element}
import org.scalajs.dom.ext._
import org.scalajs.dom.document
import org.scalajs.dom.window.console
import quicksignals._

object Main {
  extension(node: Node) {
    def setChildren(children: Seq[Node]): Unit = {
      node.childNodes.toSeq foreach node.removeChild
      children foreach node.appendChild
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
  
    val counters = Source[Seq[Counter]](Seq())
    
    val newButton = document.createElement("button")
    newButton.innerText = "New"
    document.body.appendChild(newButton)
    
    val list = document.createElement("ul")
    document.body.appendChild(list)
    
    newButton.addEventListener("click", { _ =>
      counters() = counters.now :+ Counter()
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

    children foreach { children =>
      list.setChildren(children)
    }
  }
}

