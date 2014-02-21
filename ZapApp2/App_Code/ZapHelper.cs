using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.Net;
using System.IO;
using System.Text;
using System.Runtime.Serialization.Json;


namespace org.zap
{
    public class ZapHelper
    {
        string key = "52ddafbe3ee659bad97fcce7c53592916a6bfd73";
        string resultLimit = "10";

        public List<ResultCombo> mainExecute(int num, float amt)
        {
            List<Result> results = MakeRequest();
            if (results.Count < num)
                return null;
            List<ResultCombo> l = generateCombinations(results,num);
            l.Sort(delegate(ResultCombo x, ResultCombo y)
            {
                return (Math.Abs(x.sum-amt)).CompareTo(Math.Abs(y.sum-amt));
            });
            return l;
        }

        public List<ResultCombo> generateCombinations(List<Result> myList, int size)
        {
            List<ResultCombo> combinations = new List<ResultCombo>();
            for (int a = 0; a < 2; a++)
            {
                for (int i = 0; i < (myList.Count() - (size - 1)); i++)
                {
                    float count = 0.00f;
                    List<Result> combination = new List<Result>();
                    int j = i;
                    for (; j < i + size - 1; j++)
                    {
                        combination.Add(myList.ElementAt(j));
                        string s = myList.ElementAt(j).price;
                        float c = float.Parse(s.Substring(1, s.Length - 1));
                        count += c;
                    }

                    for (int k = j; k < myList.Count(); k++)
                    {
                        string s = myList.ElementAt(k).price;
                        float c = float.Parse(s.Substring(1, s.Length - 1));
                        List<Result> temp = combination.ToList();
                        temp.Add(myList.ElementAt(k));
                        float f = count;
                        f += c;
                        
                        if (temp.Count() == size)
                        {
                            ResultCombo rc = new ResultCombo();
                            rc.comb = temp;
                            rc.sum = f;
                            combinations.Add(rc);
                        }
                    }
                }
                myList.Reverse();
            }
            return combinations;
        }

        private string createUrl()
        {
            string url = "http://api.zappos.com/Search?limit=" + resultLimit + "&key=" + key;
            return url;
        }

        private List<Result> MakeRequest()
        {
            try
            {
                string requestUrl = createUrl();
                HttpWebRequest request = WebRequest.Create(requestUrl) as HttpWebRequest;
                
                using (HttpWebResponse response = request.GetResponse() as HttpWebResponse)
                {
                    if (response.StatusCode != HttpStatusCode.OK)
                        throw new Exception(String.Format(
                        "Server error (HTTP {0}: {1}).",
                        response.StatusCode,
                        response.StatusDescription));
                
                    Stream stream1 = response.GetResponseStream();
                    StreamReader sr = new StreamReader(stream1);
                    string strsb = sr.ReadToEnd();
                    dynamic objResponse = JsonConvert.DeserializeObject(strsb);
                    JArray arr = (JArray)objResponse.SelectToken("results");
                    return arr.ToObject<List<Result>>();
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
                return null;
            }
        }

    }

    public class Result
    {
        public string price { get; set; }
        public string productName { get; set; }
        public string productId { get; set; }
    }

    public class ResultCombo 
    {
        public List<Result> comb { get; set; }
        public float sum { get; set; }
    }


}